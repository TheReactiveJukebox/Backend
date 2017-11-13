package de.reactivejukebox.recommendations.strategies;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;
import de.reactivejukebox.JukeboxConfig;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.Tracks;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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

        if (JukeboxConfig.spotifyAuthToken == null) {
            JukeboxConfig.spotifyAuthToken = this.getSpotifyToken();
        }

        String seedTracks = seeds.stream()
                .collect(Collectors.joining(","));

        Client client = Client.create();
        WebResource webResource = client.resource("https://api.spotify.com/v1/recommendations?seed_tracks=" + seedTracks + "&limit=100");
        ClientResponse response = webResource.header("Authorization", "Bearer " + JukeboxConfig.spotifyAuthToken).get(ClientResponse.class);

        String ret = response.getEntity(String.class);
        client.destroy();

        JSONObject jsonObject = new JSONObject(ret);
        JSONArray jsonArray = jsonObject.getJSONArray("tracks");

        List<String> spotifyIds = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject o = jsonArray.getJSONObject(i);
            spotifyIds.add(o.getString("id"));
        }

        return Model.getInstance().getTracks().stream()
                .filter(track -> spotifyIds.contains(track.getSpotifyId()));
    }

    private String getSpotifyToken() {
        byte[] authorization = Base64.encode(JukeboxConfig.spotifyClientId + ":" + JukeboxConfig.spotifyClientSecret);
        String authString = new String(authorization);
        Client client = Client.create();

        WebResource webResource = client.resource("https://accounts.spotify.com/api/token");

        ClientResponse response = webResource.queryParam("grant_type", "client_credentials")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + authString)
                .post(ClientResponse.class);

        if (response.getStatus() != 200) {
            System.err.println(response);
            return null;
        }

        String tokenJson = response.getEntity(String.class);
        String token = new JSONObject(tokenJson).getString("access_token");

        client.destroy();
        return token;
    }
}
