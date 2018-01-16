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

public class GenreNN extends GenreStrategy implements RecommendationStrategy {

    private List<Integer> queryGenre;
    private int requestedResults = 0;
    private Tracks tracks = Model.getInstance().getTracks();
    private final String SQL_QUERY_SIM_GENRE1 = "SELECT genresimilarity.Similarity AS sim, genresimilarity.GenreId2 AS id " +
            "FROM genresimilarity WHERE GenreId1=?";
    private final String SQL_QUERY_SIM_GENRE2 = "SELECT genresimilarity.Similarity AS sim, genresimilarity.GenreId1 AS id " +
            "FROM genresimilarity WHERE GenreId2=?";
    private final String SQL_QUERY_GENRE_TRACKS = "SELECT song_genre.SongId AS id FROM song_genre WHERE GenreId=?";
    private final String SQL_QUERY_GENRE_TRACKS_GENERIC = "SELECT song_genre.SongId AS id FROM song_genre WHERE ";

    public GenreNN(Radio radio, Collection<Track> seedTracks, int requestedResults) {
        super();
        queryGenre = getListOfRequestedGenre(radio);
        this.requestedResults = requestedResults;
    }

    @Override
    public Recommendations getRecommendations() {
        List<Track> recommendation = new ArrayList<>();
        List<Float> weights = new ArrayList<>();
        if (queryGenre.isEmpty()) {
            return new Recommendations(recommendation, weights);
        }
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();

            //fetch all songs from requested genre
            String query = SQL_QUERY_GENRE_TRACKS_GENERIC;

            for (int i = 0; i < queryGenre.size(); i++) {
                query = query + "GenreId=" + queryGenre.get(i) + " OR";
            }
            query = query.substring(0, query.length() - 3);
            PreparedStatement stmnt = con.prepareStatement(query);
            ResultSet rs = stmnt.executeQuery();

            while (rs.next() && recommendation.size() <= this.requestedResults) {
                int id = rs.getInt("id");
                recommendation.add(this.tracks.get(id));
                weights.add(1f);
            }

            //check if there are already enough tracks
            if (recommendation.size() >= this.requestedResults) {
                con.close();
                return new Recommendations(recommendation.subList(0, requestedResults), weights.subList(0, requestedResults));
            }

            //search for more tracks in the most similar genre
            List<Integer> alreadyContainded = new ArrayList<>(queryGenre);
            List<List<Integer>> otherGenre = new ArrayList<>();
            List<Map<Integer, Float>> otherGenreSims = new ArrayList<>();
            //fetch lists of most similar genre for each requested genre
            for (Integer genre : queryGenre) {
                Map<Integer, Float> myMap = this.getMostSimilarGenre(genre);
                otherGenreSims.add(myMap);
                otherGenre.add(otherGenreSims.get(otherGenreSims.size() - 1).keySet().stream()
                        .sorted(Comparator.comparing((Integer t) -> 1. - myMap.get(t)))
                        .collect(Collectors.toList()));
            }
            finishBreak:
            for (int i = 0; i < otherGenre.get(0).size(); i++) {
                for (int j = 0; j < queryGenre.size(); j++) {
                    stmnt = con.prepareStatement(SQL_QUERY_GENRE_TRACKS);
                    stmnt.setInt(1, otherGenre.get(i).get(j));
                    rs = stmnt.executeQuery();
                    alreadyContainded.add(otherGenre.get(i).get(j));
                    while (rs.next() && recommendation.size() <= this.requestedResults) {
                        int id = rs.getInt("id");
                        recommendation.add(this.tracks.get(id));
                        weights.add(otherGenreSims.get(i).get(j));
                    }
                    if (recommendation.size() >= this.requestedResults) {
                        con.close();
                        break finishBreak;
                    }
                }
            }

            con.close();
        } catch (SQLException e1) {
            System.err.println("could not fetch songs with the queried genre from database");
        }
        int subListsize = Math.min(recommendation.size(), requestedResults);
        return new Recommendations(recommendation.subList(0, subListsize), weights.subList(0, subListsize));
    }

    //Query the database for the similarity to all other genre
    private Map<Integer, Float> getMostSimilarGenre(int genre) {
        Map<Integer, Float> result = new HashMap<>();
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement stmnt = con.prepareStatement(SQL_QUERY_SIM_GENRE1);
            stmnt.setInt(1, genre);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                result.putIfAbsent(rs.getInt("id"), rs.getFloat("sim"));
            }
            stmnt = con.prepareStatement(SQL_QUERY_SIM_GENRE2);
            stmnt.setInt(1, genre);
            rs = stmnt.executeQuery();
            while (rs.next()) {
                result.putIfAbsent(rs.getInt("id"), rs.getFloat("sim"));
            }
            con.close();
        } catch (SQLException e1) {
            System.err.println("could not fetch genre similarity table from database");
        }
        return result;
    }

}
