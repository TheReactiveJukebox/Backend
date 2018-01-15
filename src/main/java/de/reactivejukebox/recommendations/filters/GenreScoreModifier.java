package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class GenreScoreModifier {


        private HashMap<String, Integer> nameIdMapping;
        float[][] similarity;
        private final String SQL_QUERY_GENRE_SIM = "SELECT genresimilarity.Similarity AS sim " +
                "FROM genresimilarity WHERE GenreId1=? AND GenreId2=?";
        private final String SQL_QUERY_GENRE_ID = "SELECT genre.id AS id, genre.name AS name FROM genre ";
        private  static GenreScoreModifier instance;

        private GenreScoreModifier() {
            initGenreIdMaps();
            similarity = new float[nameIdMapping.size()+1][nameIdMapping.size()+1];
            for (float[] row: similarity) {
                Arrays.fill(row, 0);
            }
        }

        public static GenreScoreModifier getInstance() {
            if (GenreScoreModifier.instance == null) {
                GenreScoreModifier.instance = new GenreScoreModifier();
            }
            return GenreScoreModifier.instance;
        }

        public Map<Track, Float> modifyScoreByGenreSim(Radio radio, Map<Track, Float> recommendations) {
            List<String> requestedGenre;
            if(radio.getGenres() == null || radio.getGenres().length == 0) {
                return recommendations;
            }
            requestedGenre = Arrays.asList(radio.getGenres());
            //fetch genre similarity for each relevant genre
            fetchSimilarities(requestedGenre, recommendations.keySet().stream().flatMap((Track t) ->
                    t.getGenres().stream()).distinct().collect(Collectors.toList()));

            //multiply each score with 1 + similarity (note: max(similarity)=1)
            List<Integer> requestedGenreIds = requestedGenre.stream().map((String s) -> nameIdMapping.get(s.toLowerCase()))
                    .distinct().collect(Collectors.toList());
            List<Integer> trackGenreIds;
            toContinue:
            for (Track t: recommendations.keySet()) {
                List<String> trackGenre = t.getGenres();
                for (String s: requestedGenre) {
                    if (trackGenre.contains(s)) {
                        float currentScore = recommendations.get(t);
                        recommendations.put(t, currentScore * (1 + 1));
                        continue toContinue;
                    }
                }
                float bestScore = 0;
                for (Integer genre: trackGenre.stream().map((String s) -> nameIdMapping.get(s))
                        .collect(Collectors.toList())) {
                    for (Integer requestedId: requestedGenreIds) {
                        if(similarity[genre][requestedId] > bestScore) {
                            bestScore = similarity[genre][requestedId];
                        }
                    }
                }
                float currentScore = recommendations.get(t);
                recommendations.put(t, currentScore * (1 + bestScore));
            }

            return recommendations;
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
                    name = rs.getString("name").toLowerCase();
                    id = rs.getInt("id");
                    nameIdMapping.put(name, id);
                }
                con.close();
            } catch (SQLException e) {
                System.err.println("Was not able to fetch all genre from database table genre");
            }
        }

        private void fetchSimilarities(List<String> requestedGenre, List<String> remainingGenre) {
            List<Integer> mainGenreIds = requestedGenre.stream().map((String s) -> nameIdMapping.get(s.toLowerCase()))
                    .distinct().collect(Collectors.toList());
            List<Integer> remainingGenreIds = remainingGenre.stream().map((String s) -> nameIdMapping.get(s.toLowerCase()))
                    .distinct().collect(Collectors.toList());
            float sim;
            try {
                Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
                PreparedStatement stmnt = con.prepareStatement(SQL_QUERY_GENRE_SIM);
                for(Integer remaining: remainingGenreIds) {
                    for (int mainGenreInt : mainGenreIds) {
                        if (remaining == mainGenreInt) {
                            continue;
                        }
                        //only query database if necessary
                        if(similarity[remaining][mainGenreInt] == 0) {
                            stmnt.setInt(1, Math.min(mainGenreInt, remaining));
                            stmnt.setInt(2, Math.max(mainGenreInt, remaining));
                            ResultSet rs = stmnt.executeQuery();
                            rs.next();
                            sim = rs.getFloat("sim");
                            similarity[remaining][mainGenreInt] = sim;
                            similarity[mainGenreInt][remaining] = sim;
                        }
                    }
                }
                con.close();
            } catch (SQLException e1) {
                System.err.println("Could not fetch genre similarity from database table genresimilarity");
            }
        }
    }

