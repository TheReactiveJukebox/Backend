package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.JukeboxConfig;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.Tracks;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.Recommendations;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class SpotifySongRecommender implements RecommendationStrategy {
    private final static int ConnectionTimeout = 800; // in milliseconds
    private List<String> base;
    private int resultCount;
    private Tracks tracks;
    private Radio radio;
    private Collection<Track> upcoming;

    public SpotifySongRecommender(Radio radio, Collection<Track> upcoming, int resultCount) {
        this(radio, upcoming, resultCount, Model.getInstance().getTracks());
    }

    public SpotifySongRecommender(Radio radio, Collection<Track> upcoming, int resultCount, Tracks tracks) {
        this.resultCount = resultCount;
        this.radio = radio;
        this.upcoming = upcoming;
        this.base = radio.getStartTracks().stream()
                .distinct()
                .map(Track::getSpotifyId)
                .collect(Collectors.toList());
        this.tracks = tracks;

        //Hotfix for bad Spotify DB entry
        this.base.removeIf((String s) -> s.trim().compareTo("0") == 0);
    }

    @Override
    public Recommendations getRecommendations() {
        ArrayList<Track> tracks = new ArrayList<>();
        ArrayList<Float> scores = new ArrayList<>();

        // return empty list if there are no startTracks given
        if (base.size() == 0) {
            return new Recommendations(tracks, scores);
        }

        JSONArray jsonArray = spotifyApiCall(base);

        int count = jsonArray.length();
        for (int i = 0; i < count; i++) {
            // get next track
            JSONObject o = jsonArray.getJSONObject(i);
            Track t = Model.getInstance().getTracks().getBySpotifyId(o.getString("id"));

            // filter upcoming tracks and tracks we don't have in the library
            if (t == null || upcoming.contains(t)) {
                continue;
            }

            // add track to recommendations, score according to position
            tracks.add(t);
            scores.add(1.0f - ((float) i / (float) count) * 0.5f);
        }

        if (tracks.size() <= resultCount)
            return new Recommendations(tracks, scores);
        return new Recommendations(tracks.subList(0, resultCount), scores.subList(0, resultCount));
    }

    /**
     * Wraps both Spotify API calls (token + recommendations) with some error handling
     *
     * @param seeds list of Spotify IDs of seed tracks
     * @return the JSON response of the recommendation call or an empty JSONArray if something goes wrong
     */
    private JSONArray spotifyApiCall(List<String> seeds) {
        // cache auth token globally, only needs to be executed once
        if (JukeboxConfig.SPOTIFY_AUTH_TOKEN == null) {
            try {
                JukeboxConfig.SPOTIFY_AUTH_TOKEN = getSpotifyAuthToken(
                        JukeboxConfig.SPOTIFY_CLIENT_ID,
                        JukeboxConfig.SPOTIFY_CLIENT_SECRET);
            } catch (Exception e) {
                System.err.println("Could not obtain auth token from Spotify, returning empty stream. Exception: ");
                e.printStackTrace();
                return new JSONArray();
            }
        }

        // get Spotify recommendations
        try {
            return getSpotifyRecommendations(seeds, JukeboxConfig.SPOTIFY_AUTH_TOKEN);
        } catch (Exception e) {
            System.err.println("Could not get tracks from Spotify, returning empty list. Exception: ");
            e.printStackTrace();
            return new JSONArray();
        }
    }

    /**
     * Queries the Spotify API for an auth token for this client
     *
     * @param clientId     Spotify client ID
     * @param clientSecret Spotify client secret
     * @return an auth token that is needed to make other API calls
     * @throws IOException when Spotify returns an error
     */
    private String getSpotifyAuthToken(String clientId, String clientSecret) throws IOException {
        // encode client ID and secret as base64
        byte[] authorization = Base64.getEncoder().encode(
                (clientId + ":" + clientSecret).getBytes());
        String authString = new String(authorization);

        // make request using the "Client Credentials" flow
        String jsonResponse = Request.Post("https://accounts.spotify.com/api/token")
                .connectTimeout(ConnectionTimeout)
                .bodyForm(Form.form().add("grant_type", "client_credentials").build())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + authString)
                .execute()
                .returnContent()
                .asString();

        return new JSONObject(jsonResponse).getString("access_token");
    }

    /**
     * Queries the Spotify API for recommended tracks
     *
     * @param seeds            collection of tracks to use as a base for recommendations
     * @param spotifyAuthToken the auth token obtained by getSpotifyAuthToken()
     * @return the JSON response from Spotify
     * @throws IOException when Spotify returns an error
     */
    private JSONArray getSpotifyRecommendations(Collection<String> seeds, String spotifyAuthToken) throws IOException {
        // create a comma-separated list of spotify ids
        String seedTracks = seeds.stream()
                .collect(Collectors.joining(","));

        try {
            // make request; limit = 100 because that is the maximum defined by Spotify
            String spotifyResponse = Request
                    .Get("https://api.spotify.com/v1/recommendations?seed_tracks=" + seedTracks + "&limit=100")
                    .addHeader("Authorization", "Bearer " + spotifyAuthToken)
                    .connectTimeout(ConnectionTimeout)
                    .execute()
                    .returnContent()
                    .asString();
            return new JSONObject(spotifyResponse).getJSONArray("tracks");
        } catch (HttpResponseException e) {
            System.err.println("Spotify API call error: get Code: " + e.getStatusCode());
            return new JSONArray();
        } catch (SocketTimeoutException e) {
            System.err.println("Spotify API call timeout.");
            return new JSONArray();
        }
    }
}
