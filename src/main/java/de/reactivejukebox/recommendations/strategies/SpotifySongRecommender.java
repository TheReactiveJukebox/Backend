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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SpotifySongRecommender implements RecommendationStrategy {

    private Collection<Track> base;
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
        this.base = radio.getStartTracks();
        this.tracks = tracks;
    }

    @Override
    public List<Track> getRecommendations() {
        return getSpotifyRecommendation(base.stream().distinct().collect(Collectors.toList()));
    }

    private List<Track> getSpotifyRecommendation(List<Track> seeds) {
        Stream<Track> possibleTracks = spotifyApiCall(seeds);
        possibleTracks = radio.filter(possibleTracks);  // Filter for radio properties

        return radio.filterHistory(possibleTracks, upcoming, resultCount) // Filter History
                .collect(Collectors.toList());
    }

    private Stream<Track> spotifyApiCall(List<Track> seeds) {

        if (JukeboxConfig.spotifyAuthToken == null) {
            JukeboxConfig.spotifyAuthToken = this.getSpotifyToken();
        }

        Client client = Client.create();

        WebResource webResource = client.resource("https://api.spotify.com/v1/recommendations?seed_tracks=0c6xIDDpzE81m2q797ordA&limit=100");

        ClientResponse response = webResource.header("Authorization", "Bearer " + JukeboxConfig.spotifyAuthToken).get(ClientResponse.class);

        String ret = response.getEntity(String.class);

        System.out.println(ret);


        return this.tracks.stream();

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
            System.out.println(response);
            System.exit(1);
        }

        String token = response.getEntity(String.class).substring(17, 103);
        client.destroy();
        return token;
    }

    /* example from SAGH
    public List<Track> getRecommendations() {
        return base.stream()
                .map(Track::getArtist) // get artist for each track
                .distinct() // eliminate duplicates
                .flatMap(this::greatestHits) // get every artist's greatest hits in a single stream
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed()) // sort by play count, descending
                .limit(resultCount) // get first resultCount Tracks
                .collect(Collectors.toList()); // collect into list
    }
    */
    /* example from SAGH
    private Stream<Track> greatestHits(Artist a) {
        // historyFilter by History and Genre
        Stream<Track> possibleTracks = tracks.stream().filter(new ArtistPredicate(a));  // Filter by Artists
        possibleTracks = radio.filter(possibleTracks);  // Filter for radio properties
        return radio.filterHistory(possibleTracks, upcoming, resultCount) //Filter History
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed()); // sort
    }
    */
}
