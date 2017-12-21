package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.Tracks;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.Recommendations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class TrackFeatureDistance implements RecommendationStrategy {

    private final int IGNORE_COUNTER = 5;
    private  int fetch_counter = 100;
    private final String SQL_QUERY_RECOMMEND = "SELECT feature_distance.track_to AS id, distance " +
            "FROM feature_distance WHERE track_from=? ORDER BY distance ASC ";
    private int requestedResults;
    private Tracks tracks;
    private Collection<Track> seedTracks;
    private Radio radio;


    /**
     * Creates a recommendations based on the similarity of two songs by theirs features.
     *
     * @param seedTracks       collection of songs the radio will play
     * @param requestedResults number of recommendations to deliver
     */
    public TrackFeatureDistance(Radio radio, Collection<Track> seedTracks, int requestedResults) {
        this.requestedResults = requestedResults;
        this.fetch_counter = Math.max(requestedResults, fetch_counter);
        this.tracks = Model.getInstance().getTracks();
        this.radio = radio;
        if (seedTracks != null && !seedTracks.isEmpty()) {
            // use next songs for recommendation
            this.seedTracks = seedTracks;
        } else if (radio != null && radio.getStartTracks() != null && !radio.getStartTracks().isEmpty()) {
            // use the selected start tracks
            this.seedTracks = radio.getStartTracks();
        } else if (radio != null) {
            //use the 3 most similar songs as seed tracks
            float arousal, valence, speed, dynamic;
            if (radio.getArousal() != null) {
                arousal = radio.getArousal();
            } else {
                arousal = tracks.stream()
                        .collect(Collectors.averagingDouble((Track track) -> track.getArousal())).floatValue();
            }
            if (radio.getValence() != null) {
                valence = radio.getValence();
            } else {
                valence = tracks.stream()
                        .collect(Collectors.averagingDouble((Track track) -> track.getValence())).floatValue();
            }
            if (radio.getDynamic() != null) {
                dynamic = radio.getDynamic();
            } else {
                dynamic = tracks.stream()
                        .collect(Collectors.averagingDouble((Track track) -> track.getDynamic())).floatValue();
            }
            if (radio.getMaxSpeed() != null && radio.getMinSpeed() != null) {
                speed = (radio.getMaxSpeed() + radio.getMinSpeed()) / 2;
            } else {
                speed = tracks.stream()
                        .collect(Collectors.averagingDouble((Track track) -> track.getSpeed())).floatValue();
            }
            this.seedTracks = this.tracks.stream().sorted(Comparator.comparing((Track o1) ->
                    Math.abs(o1.getDynamic() - dynamic) + Math.abs(o1.getSpeed() - speed)
                            + Math.abs(o1.getValence() - valence) + Math.abs(o1.getArousal() - arousal)))
                    .limit(3).collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("There need to give at least one parameter");
        }
    }

    @Override
    public Recommendations getRecommendations() {
        if (seedTracks.isEmpty()) {
            return new Recommendations(new ArrayList<>(), new ArrayList<>());
        }
        List<Map<Integer, Double>> potRecommendations = new ArrayList<>(seedTracks.size());
        for (Track track : seedTracks) {
            potRecommendations.add(fetchScoredSongs(track));
        }
        //reduce maps to only one map
        Map<Integer, Double> resultMap = potRecommendations.get(0);
        for (int i = 1; i < potRecommendations.size(); i++) {
            Map<Integer, Double> toAdd = potRecommendations.get(i);
            for (Map.Entry<Integer, Double> entry : toAdd.entrySet()) {
                Double currentValue = resultMap.putIfAbsent(entry.getKey(), entry.getValue());
                if (currentValue != null && currentValue < entry.getValue()) {
                    resultMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        //transform to sorted tracks
        List<Track> recommendations = resultMap.entrySet().stream().sorted(Comparator.comparing((Map.Entry<Integer, Double> o1) -> o1.getValue()))
                .map((Map.Entry<Integer, Double> id) -> tracks.get(id.getKey()))
                .collect(Collectors.toList());
        // only use the first and apply ranking = weighting
        recommendations = this.radio.filter(recommendations.stream())
                .limit(requestedResults).collect(Collectors.toList());
        //create list with weights by range normalizing distances and save them as weights
        List<Double> recommendationsWeights = recommendations.stream()
                .map((Track track) -> resultMap.get(track.getId())).collect(Collectors.toList());
        Double max = Collections.max(recommendationsWeights);
        Double min = Collections.min(recommendationsWeights);
        List<Float> finalWeights = recommendationsWeights.stream()
                .map((Double in) -> (in - min) * (1 / max)).map(Double::floatValue).collect(Collectors.toList());
        return new Recommendations(recommendations, finalWeights);
    }

    private Map<Integer, Double> fetchScoredSongs(Track track) {
        Map<Integer, Double> result = new HashMap<>();
        int id;
        double distance;
        int counter = 1;
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement stmnt = con.prepareStatement(SQL_QUERY_RECOMMEND);
            stmnt.setInt(1, track.getId());
            ResultSet rs = stmnt.executeQuery();
            while (rs.next() && result.size() <= IGNORE_COUNTER + fetch_counter) {
                // ignore the first results, so we do not deliver other versions of a song or too simliar ones
                if (IGNORE_COUNTER < counter++) {
                    id = rs.getInt("id");
                    distance = rs.getDouble("distance");
                    result.put(id, distance);
                }
            }
            con.close();
        } catch (SQLException e1) {
            System.err.println("could not fetch song feature distance table from database");
        }
        return result;
    }
}