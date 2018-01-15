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

public class GenreNN implements RecommendationStrategy {

    private List<Integer> queryGenre;
    private HashMap<String, Integer> nameIdMapping;
    private int requestedResults = 0;
    private Tracks tracks = Model.getInstance().getTracks();
    private final String SQL_QUERY_SIM_GENRE1 = "SELECT genresimilarity.Similarity AS sim, genresimilarity.GenreId2 AS id " +
            "FROM genresimilarity WHERE GenreId1=?";
    private final String SQL_QUERY_SIM_GENRE2 = "SELECT genresimilarity.Similarity AS sim, genresimilarity.GenreId1 AS id " +
            "FROM genresimilarity WHERE GenreId2=?";
    private final String SQL_QUERY_GENRE_ID = "SELECT genre.id AS id, genre.name AS name FROM genre ";
    private final String SQL_QUERY_GENRE_TRACKS = "SELECT song_genre.SongId AS id FROM song_genre WHERE GenreId=?";
    private final String SQL_QUERY_GENRE_TRACKS_GENERIC = "SELECT song_genre.SongId AS id FROM song_genre WHERE";

    public GenreNN(Radio radio, Collection<Track> seedTracks, int requestedResults) {
        this.initGenreIdMaps();
       if(radio.getGenres() != null && radio.getGenres().length != 0) {
            queryGenre = Arrays.asList(radio.getGenres()).stream().map((String s) -> nameIdMapping.get(s.toLowerCase())).distinct().collect(Collectors.toList());
       } else if (!seedTracks.isEmpty()) {
            queryGenre = seedTracks.stream().flatMap((Track t) -> t.getGenres().stream().map((String s) ->
                    nameIdMapping.get(s.toLowerCase()))).distinct().collect(Collectors.toList());
       } else {
            queryGenre = new ArrayList<>();
       }
       this.requestedResults = requestedResults;
    }

    @Override
    public Recommendations getRecommendations() {
        List<Track> recommendations = new ArrayList<>();
        List<Float> weights = new ArrayList<>();
        if(queryGenre.isEmpty()) {
            return new Recommendations(recommendations, weights);
        }
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            //fetch from requested genre
            String query = SQL_QUERY_GENRE_TRACKS_GENERIC;
            for (int i = 0;i < queryGenre.size(); i++) {
                query = query + "GenreId=" + queryGenre.get(i) + " OR";
            }
            query = query.substring(0, query.length() - 3);
            PreparedStatement stmnt = con.prepareStatement(query);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next() && recommendations.size() <= this.requestedResults) {
                int id = rs.getInt("id");
                recommendations.add(this.tracks.get(id));
                weights.add(1f);
            }
            if (recommendations.size() >= this.requestedResults) {
                con.close();
                return new Recommendations(recommendations, weights);
            }
            List<Integer> alreadyContainded = new ArrayList<>(queryGenre);
            //search in next similar tracks
            List<List<Integer>> otherGenre = new ArrayList<>();
            List<Map<Integer, Float>> otherGenreSims = new ArrayList<>();
            for (Integer genre: queryGenre) {
                Map<Integer, Float> myMap = this.getMostSimilarGenre(genre);
                otherGenreSims.add(myMap);
                otherGenre.add(otherGenreSims.get(otherGenreSims.size()-1).keySet().stream()
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
                    while (rs.next() && recommendations.size() <= this.requestedResults) {
                        int id = rs.getInt("id");
                        recommendations.add(this.tracks.get(id));
                        weights.add(otherGenreSims.get(i).get(j));
                    }
                    if(recommendations.size() >= this.requestedResults) {
                        con.close();
                        break finishBreak;
                    }
                }
            }

            con.close();
        } catch (SQLException e1) {
            System.err.println("could not fetch song feature distance table from database");
        }
        return new Recommendations(recommendations, weights);
    }

    private Map<Integer,Float> getMostSimilarGenre(int genre) {
        Map<Integer, Float> result = new HashMap<>();
        int id;
        double sim;
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
            while (rs.next() ) {
                result.putIfAbsent(rs.getInt("id"), rs.getFloat("sim"));
            }
            con.close();
        } catch (SQLException e1) {
            System.err.println("could not fetch song feature distance table from database");
        }
        return result;
    }

    private void initGenreIdMaps() {
        nameIdMapping = new HashMap<>();
        Connection con;
        try {
            con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement stmnt = con.prepareStatement(SQL_QUERY_GENRE_ID);
            ResultSet rs = stmnt.executeQuery();
            String name;
            Integer id;
            while (rs.next()) {
                name = rs.getString("name");
                id = rs.getInt("id");
                nameIdMapping.put(name, id);
            }
            con.close();
        } catch (SQLException e) {
            System.err.println("Was not able to fetch all genre from database table genre");
        }
    }
}
