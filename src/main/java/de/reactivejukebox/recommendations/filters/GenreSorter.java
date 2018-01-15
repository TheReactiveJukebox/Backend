package de.reactivejukebox.recommendations.filters;

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

public class GenreSorter {

    private HashMap<String, Integer> nameIdMapping;
    private final String SQL_QUERY_GENRE_SIM = "SELECT genresimilarity.Similarity AS sim " +
            "FROM genresimilarity WHERE GenreId1=? AND GenreId2=?";
    private final String SQL_QUERY_GENRE_ID = "SELECT genre.id AS id, genre.name AS name FROM genre ";
    private  static GenreSorter instance;

    private GenreSorter() {
        initGenreIdMaps();
    }

    public static GenreSorter getInstance() {
        if (GenreSorter.instance == null) {
            GenreSorter.instance = new GenreSorter();
        }
            return GenreSorter.instance;
    }

    public Recommendations getGenreSortedRecommendation(Radio radio, List<Track> recommendations, List<Float> scores) {
        List<String> requested_genre;
        if(radio.getGenres() == null || radio.getGenres().length == 0) {
            return new Recommendations(recommendations, scores);
        }
        requested_genre = Arrays.asList(radio.getGenres());
        //fetch genre similarity for each relevant genre
        List<String> simSortedGenreList = getSortedGenreList(requested_genre,
                recommendations.stream().flatMap((Track t) -> t.getGenres().stream())
                        .distinct().collect(Collectors.toList()));

        //fill new lists sorted by genre
        List<Track> sortedTracks = new ArrayList<>(recommendations.size());
        List<Float> sortedScores = new ArrayList<>(scores.size());
        Track currentTrack;
        List<Integer> toRemove = new ArrayList<>();

        //first add all songs containing the concrete genre
        for (int i = 0; i < recommendations.size(); i++) {
            currentTrack = recommendations.get(i);
            tobreak:
            for (String genre: requested_genre) {
                if (currentTrack.getGenres().contains(genre)) {
                    //add found song
                    sortedTracks.add(currentTrack);
                    sortedScores.add(scores.get(i));
                    //remove inserted indices
                    toRemove.add(i);
                    break tobreak;
                }
            }
        }

        //clean (start removing at the end, so that the indices do not get shuffled)
        for (int a=toRemove.size() - 1;a >= 0; a--) {
            recommendations.remove(toRemove.get(a));
            scores.remove(toRemove.get(a));
        }
        toRemove.clear();

        //now add all tracks without explicit requested genre starting with the most similar
        while (!recommendations.isEmpty() && !simSortedGenreList.isEmpty()) {
            //look weather the next relevant genre is contained in one remaining song
            for (int i = 0; i < recommendations.size(); i++) {
                currentTrack = recommendations.get(i);
                if (currentTrack.getGenres().contains(simSortedGenreList.get(0))) {
                    //add found song
                    sortedTracks.add(currentTrack);
                    sortedScores.add(scores.get(i));
                    //remove inserted indices
                    toRemove.add(i);
                }
            }
            simSortedGenreList.remove(0);
            //clean (start removing at the end, so that the indices do not get shuffled)
            for (int a=toRemove.size() - 1;a >= 0; a--) {
                recommendations.remove(toRemove.get(a));
                scores.remove(toRemove.get(a));
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
            System.err.println("GenreSorter was not able to fetch all genre from database table genre");
        }
    }

    private List<String> getSortedGenreList(List<String> requestedGenre, List<String> remainingGenre) {
        Map<String, Double> result = new HashMap<>();
        List<Integer> mainGenreIds = requestedGenre.stream().map((String s) -> nameIdMapping.get(s))
                .distinct().collect(Collectors.toList());
        int id;
        double distance;
        try {
            Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
            PreparedStatement stmnt = con.prepareStatement(SQL_QUERY_GENRE_SIM);
            for(String genre: remainingGenre) {
                for (int mainGenreInt : mainGenreIds) {
                    id = nameIdMapping.get(genre);
                    if (id == mainGenreInt) {
                        continue;
                    }
                    stmnt.setInt(1, Math.min(mainGenreInt, id));
                    stmnt.setInt(2, Math.max(mainGenreInt, id));
                    ResultSet rs = stmnt.executeQuery();
                    while (rs.next()) {
                            distance = rs.getDouble("sim");
                            result.put(genre, distance);
                    }
                }
            }
            con.close();
        } catch (SQLException e1) {
            System.err.println("GenreSorter could not fetch genre similarity from database table genresimilarity");
        }
        /* return the sorted genre list
         (note that a similarity value of 1 means, that the genre are very similiar.
         To get the correct result, we need to inverse the similarity) */
        return remainingGenre.stream().sorted(Comparator.comparing((String s) -> 1 - result.getOrDefault(s,0.0)))
                .collect(Collectors.toList());
    }
}
