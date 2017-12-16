package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.Tracks;
import de.reactivejukebox.recommendations.RecommendationStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class TrackFeatureDistance implements RecommendationStrategy {

    private static int IGNORE_COUNTER = 3;
    private static String SQL_QUERY_RECOMMEND = "SELECT  feature_distance.track_to AS id, distance FROM feature_distance WHERE track_from=? ORDER BY distance ASC ";
    Comparator<Map.Entry<String, Double>> byDistance = (Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) -> o1.getValue().compareTo(o2.getValue());
    private int requestedResults;
    private Tracks tracks;
    private Collection<Track> upcoming;

    public TrackFeatureDistance(float speed, float dynamic, int requestedResults) {
        this(Model.getInstance().getTracks().stream().sorted((Track o1, Track o2) -> Float.valueOf(Math.abs(o1.getDynamic() - dynamic) + Math.abs(o1.getSpeed() - speed)).compareTo(Float.valueOf(Math.abs(o2.getDynamic() - dynamic) + Math.abs(o2.getSpeed() - speed)))).limit(3).collect(Collectors.toList()), requestedResults);
    }


    /**
     * Creates a recommendations based on the similarity of two songs by theirs features.
     *
     * @param upcoming         collection of songs the radio will play
     * @param requestedResults number of recommendations to deliver
     */
    public TrackFeatureDistance(Collection<Track> upcoming, int requestedResults) {
        this.requestedResults = requestedResults;
        this.tracks = Model.getInstance().getTracks();
        this.upcoming = upcoming;
    }

    @Override
    public List<Track> getRecommendations() {
        List<Map<String, Double>> potRecomendations = new ArrayList<>(upcoming.size());
        for (Track track : upcoming) {
            potRecomendations.add(fetchScoredSongs(track));
        }
        //reduce maps to only one map
        Map<String, Double> resultMap = potRecomendations.get(0);
        for (int i = 1; i < potRecomendations.size(); i++) {
            Map<String, Double> toAdd = potRecomendations.get(i);
            for (Map.Entry<String, Double> entry : toAdd.entrySet()) {
                Double currentValue = resultMap.putIfAbsent(entry.getKey(), entry.getValue());
                if (currentValue != null && currentValue < entry.getValue()) {
                    resultMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        //transform to sorted tracks
        List<Track> recommendations = resultMap.entrySet().stream().sorted(byDistance).map((Map.Entry<String, Double> id) -> tracks.get(Integer.valueOf(id.getKey()))).collect(Collectors.toList());
        // only use the first and apply ranking = weighting
        recommendations = recommendations.subList(0, requestedResults - 1);
        //create list with weights by range normalizing distances and save them as weights
        List<Double> recommendationsWeights = recommendations.stream().map((Track track)  -> resultMap.get(track.getId() + "")).collect(Collectors.toList());
        Double max = Collections.max(recommendationsWeights);
        Double min = Collections.min(recommendationsWeights);
        recommendationsWeights = recommendationsWeights.stream().map((Double in) -> (in - min) * (1/max)).collect(Collectors.toList());
        return recommendations;
    }

    private Map<String, Double> fetchScoredSongs(Track track) {
        Map<String, Double> result = new HashMap<>();
        String id;
        double distance;
        int counter = 1;
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement stmnt = con.prepareStatement(SQL_QUERY_RECOMMEND);
            stmnt.setInt(1, track.getId());
            ResultSet rs = stmnt.executeQuery();
            while (rs.next() && result.size() <= requestedResults) {
                // ignore the first results, so we do not deliver other versions of a song or too simliar ones
                if (IGNORE_COUNTER < counter++) {
                    id = rs.getString("id");
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