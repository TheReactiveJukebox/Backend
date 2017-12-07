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

public class TrackFeatureDistance implements RecommendationStrategy{

        private static String SQL_QUERY = "SELECT  feature_distance.track_to AS id, distance FROM feature_distance WHERE track_from=? ORDER BY distance ASC ";
        private int requestedResults;
        private Tracks tracks;
        private Collection<Track> upcoming;
        Comparator<Map.Entry<String, Double>> byDistance = (Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)->o1.getValue().compareTo(o2.getValue());

    /**
     * Creates a recommendations based on the similarity of two songs by theirs features.
     * @param upcoming collection of songs the radio will play
     * @param requestedResults number of recommendations to deliver
     */
    public TrackFeatureDistance(Collection<Track> upcoming, int requestedResults) {
            this.requestedResults = requestedResults;
            this.tracks = Model.getInstance().getTracks();
            this.upcoming = upcoming;
        }

        @Override
        public Map<Track, Double> getRecommendations() {
            List<Map<String, Double>> potRecomendations = new ArrayList<>(upcoming.size());
            for (Track track: upcoming) {
                potRecomendations.add(fetchScoredSongs(track));
            }
            //reduce maps to only one map
            Map<String, Double> resultMap = potRecomendations.get(0);
            for (int i = 1; i < potRecomendations.size(); i++) {
                Map<String, Double> toAdd = potRecomendations.get(i);
                for (Map.Entry<String, Double> entry: toAdd.entrySet()) {
                    Double currentValue = resultMap.putIfAbsent(entry.getKey(), entry.getValue());
                    if(currentValue != null && currentValue < entry.getValue()) {
                        resultMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            //transform to sorted tracks
            List<Track> recommendations = resultMap.entrySet().stream().sorted(byDistance).map((Map.Entry<String, Double> id) -> tracks.get(Integer.valueOf(id.getKey()))).collect(Collectors.toList());
            // only use the first and apply ranking = weighting
            recommendations = recommendations.subList(0, requestedResults - 1);
            Map<Track, Double> finalRecommendation = new HashMap<>();
            double stepSize = 1/requestedResults;
            for (int i = 0; i < recommendations.size(); i++) {
                finalRecommendation.put(recommendations.get(i), 1 - i*stepSize);
            }
            return finalRecommendation;
        }

        private Map<String, Double> fetchScoredSongs(Track track) {
            Map<String, Double> result = new HashMap<>();
            String id;
            double distance;
            int counter = 1;
            try {
                Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
                PreparedStatement stmnt = con.prepareStatement(SQL_QUERY);
                stmnt.setInt(1, track.getId());
                ResultSet rs = stmnt.executeQuery();
                while (rs.next() && result.size() <= requestedResults) {
                    if(5 < counter++) {
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
