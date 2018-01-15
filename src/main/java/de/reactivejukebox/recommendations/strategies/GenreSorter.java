package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.Recommendations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class GenreSorter extends GenreStrategy {

    private final String SQL_QUERY_GENRE_SIM = "SELECT genresimilarity.Similarity AS sim " +
            "FROM genresimilarity WHERE GenreId1=? AND GenreId2=?";
    private static GenreSorter instance;

    private GenreSorter() {
        super();
    }

    public static GenreSorter getInstance() {
        if (GenreSorter.instance == null) {
            GenreSorter.instance = new GenreSorter();
        }
        return GenreSorter.instance;
    }

    public Recommendations getGenreSortedRecommendation(Radio radio, List<Track> recommendations, List<Float> scores) {
        List<String> requestedGenre = getListOfRequestedGenreStrings(radio);
        //check whether there are any relevant genre
        if (requestedGenre.isEmpty()) {
            return new Recommendations(recommendations, scores);
        }

        //fetch genre similarity for each relevant genre
        List<String> simSortedGenreList = getSortedGenreList(transformStringToInteger(requestedGenre),
                recommendations.stream()
                        .flatMap((Track t) -> t.getGenres().stream())
                        .distinct().collect(Collectors.toList())
        );

        //init new lists
        List<Track> sortedTracks = new ArrayList<>(recommendations.size());
        List<Float> sortedScores = new ArrayList<>(scores.size());
        Track currentTrack;
        List<Integer> toRemove = new ArrayList<>();

        //first add all songs containing the user specified genre
        for (int i = 0; i < recommendations.size(); i++) {
            currentTrack = recommendations.get(i);
            tobreak:
            for (String genre : requestedGenre) {
                if (currentTrack.getGenres().contains(genre)) {
                    //add matching song
                    sortedTracks.add(currentTrack);
                    sortedScores.add(scores.get(i));
                    //mark inserted indices for removal
                    toRemove.add(i);
                    break tobreak;
                }
            }
        }

        //clean input list (start removing at the end, so that the indices do not get shuffled)
        for (int a = toRemove.size() - 1; a >= 0; a--) {
            recommendations.remove((int)toRemove.get(a));
            scores.remove((int)toRemove.get(a));
        }
        toRemove.clear();

        //now add all tracks without explicit requested genre starting with the most similar
        while (!recommendations.isEmpty() && !simSortedGenreList.isEmpty()) {
            //look weather the next relevant genre is contained in one remaining song
            for (int i = 0; i < recommendations.size(); i++) {
                currentTrack = recommendations.get(i);
                if (currentTrack.getGenres().contains(simSortedGenreList.get(0))) {
                    //add matching song
                    sortedTracks.add(currentTrack);
                    sortedScores.add(scores.get(i));
                    //mark inserted indices for removal
                    toRemove.add(i);
                }
            }
            simSortedGenreList.remove(0);
            //clean (start removing at the end, so that the indices do not get shuffled)
            for (int a = toRemove.size() - 1; a >= 0; a--) {
                recommendations.remove((int)toRemove.get(a));
                scores.remove((int)toRemove.get(a));
            }
            toRemove.clear();
        }
        //add songs without any genre
        if (!recommendations.isEmpty()) {
            sortedTracks.addAll(recommendations);
            sortedScores.addAll(scores);
        }
        return new Recommendations(sortedTracks, sortedScores);
    }

    private List<String> getSortedGenreList(List<Integer> requestedGenre, List<String> remainingGenre) {
        Map<String, Float> result = new HashMap<>();
        int id;
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement stmnt = con.prepareStatement(SQL_QUERY_GENRE_SIM);
            for (String genre : remainingGenre) {
                for (int mainGenreInt : requestedGenre) {
                    id = nameIdMapping.get(genre);
                    if (id == mainGenreInt) {
                        continue;
                    }
                    //database is a triangular matrix, so make sure to use the smaller value first
                    stmnt.setInt(1, Math.min(mainGenreInt, id));
                    stmnt.setInt(2, Math.max(mainGenreInt, id));
                    ResultSet rs = stmnt.executeQuery();
                    rs.next(); //there is only exact one result
                    result.put(genre, rs.getFloat("sim"));
                }
            }
            con.close();
        } catch (SQLException e1) {
            System.err.println("GenreSorter could not fetch genre similarity from database table genresimilarity");
        }
        // return the sorted genre list
        return remainingGenre.stream()
                .sorted(Comparator.comparing((String s) -> 1 - result.getOrDefault(s, 0f)))//similarity range = (0,1) most sim. = 1 => invert similarity for correct order
                .collect(Collectors.toList());
    }
}
