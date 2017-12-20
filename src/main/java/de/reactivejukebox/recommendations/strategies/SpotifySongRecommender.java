package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.JukeboxConfig;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.Tracks;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SpotifySongRecommender implements RecommendationStrategy {

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
    }

    @Override
    public List<Track> getRecommendations() {
        Stream<Track> possibleTracks = spotifyApiCall(base);
        possibleTracks = radio.filter(possibleTracks);  // Filter for radio properties

        return radio.filterHistory(possibleTracks, upcoming, resultCount) // Filter History
                .collect(Collectors.toList());
    }

    private Stream<Track> spotifyApiCall(List<String> seeds) {

        if (JukeboxConfig.SPOTIFY_AUTH_TOKEN == null) {
            try {
                JukeboxConfig.SPOTIFY_AUTH_TOKEN = getSpotifyToken();
            } catch (IOException e) {
                System.err.println("Could not obtain auth token from Spotify, returning empty stream. Exception: ");
                e.printStackTrace();
                return Stream.empty();
            }
        }

        String seedTracks = seeds.stream()
                .collect(Collectors.joining(","));

        try {
            String spotifyResponse = Request
                    .Get("https://api.spotify.com/v1/recommendations?seed_tracks=" + seedTracks + "&limit=100") // TODO how does this work if it's hard limited to 100 tracks?
                    .addHeader("Authorization", "Bearer " + JukeboxConfig.SPOTIFY_AUTH_TOKEN)
                    .connectTimeout(800)
                    .execute()
                    .returnContent()
                    .asString();

            JSONObject jsonObject = new JSONObject(spotifyResponse);
            JSONArray jsonArray = jsonObject.getJSONArray("tracks");

            List<String> spotifyIds = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                spotifyIds.add(o.getString("id"));
            }

            return Model.getInstance().getTracks().stream()
                    .filter(track -> spotifyIds.contains(track.getSpotifyId()));
        } catch (IOException e) {
            System.err.println("Could not get tracks from Spotify, returning empty stream. Exception: ");
            e.printStackTrace();
            return Stream.empty();
        }
    }

    private String getSpotifyToken() throws IOException {
        // encode client ID and secret as base64

        byte[] authorization = Base64.getEncoder().encode(
                (JukeboxConfig.SPOTIFY_CLIENT_ID + ":" + JukeboxConfig.SPOTIFY_CLIENT_SECRET).getBytes());

        String authString = new String(authorization);//.replace("\r\n", "");
        String jsonResponse = Request.Post("https://accounts.spotify.com/api/token")
                .connectTimeout(800)
                .bodyForm(Form.form().add("grant_type", "client_credentials").build())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + authString)
                .execute()
                .returnContent()
                .asString();

        return new JSONObject(jsonResponse).getString("access_token");
    }
}
