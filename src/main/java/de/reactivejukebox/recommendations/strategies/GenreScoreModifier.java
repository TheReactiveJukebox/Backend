package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class GenreScoreModifier extends GenreStrategy{


        // matrix with similarity between each genre (lazy init)
        float[][] similarity;
        //query to get similarity between exactly two genre
        private final String SQL_QUERY_GENRE_SIM = "SELECT genresimilarity.Similarity AS sim " +
            "FROM genresimilarity WHERE GenreId1=? AND GenreId2=?";
        private  static GenreScoreModifier instance;

        private GenreScoreModifier() {
            super();
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
            List<Integer> requestedGenre  = getListOfRequestedGenre(radio);
            if(requestedGenre.isEmpty()) {
                return recommendations;
            }
            //fetch genre similarity for each relevant genre
            fetchSimilarities(requestedGenre, recommendations.keySet().stream().flatMap((Track t) ->
                    t.getGenres().stream()).distinct().collect(Collectors.toList()));

            //multiply each score with 1 + similarity (note: most similar=1)
            List<Integer> trackGenre;
            toContinue:
            for (Track t: recommendations.keySet()) {
                trackGenre = transformStringToInteger(t.getGenres());
                // update track score with the exactly requested genre
                for (Integer rgenre: requestedGenre) {
                    if (trackGenre.contains(rgenre)) {
                        float currentScore = recommendations.get(t);
                        recommendations.put(t, currentScore * (1 + 1));
                        continue toContinue;
                    }
                }
                // update score ot tracks not containing the exact genre
                float bestScore = 0;
                for (Integer genre: trackGenre) {
                    for (Integer requestedId: requestedGenre) {
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

        private void fetchSimilarities(List<Integer> requestedGenre, List<String> remainingGenre) {
            List<Integer> remainingGenreIds = transformStringToInteger(remainingGenre);
            float sim;
            try {
                Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
                PreparedStatement stmnt = con.prepareStatement(SQL_QUERY_GENRE_SIM);
                for(Integer remaining: remainingGenreIds) {
                    for (int mainGenreInt : requestedGenre) {
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

