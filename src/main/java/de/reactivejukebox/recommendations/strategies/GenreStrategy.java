package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GenreStrategy {


    private final String SQL_QUERY_GENRE_ID = "SELECT genre.id AS id, genre.name AS name FROM genre ";
    //mapping from string representation of genre to int
    protected HashMap<String, Integer> nameIdMapping;

    protected  GenreStrategy() {
        initGenreIdMaps();
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

    protected List<Integer> getListOfRequestedGenre(Radio radio){
        return transformStringToInteger(getListOfRequestedGenreStrings(radio));
    }

    protected List<String> getListOfRequestedGenreStrings(Radio radio){
        if (radio.getGenres() != null && radio.getGenres().length != 0) {
            return Arrays.asList(radio.getGenres());
        } else if (radio.getStartTracks() != null && !radio.getStartTracks().isEmpty()) {
            return radio.getStartTracks().stream().flatMap((Track t) -> t.getGenres().stream())
                    .distinct().collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    protected List<Integer> transformStringToInteger(List<String> genreList) {
        return genreList.stream().map((String s) -> nameIdMapping.get(s.toLowerCase()))
                .distinct().collect(Collectors.toList());
    }
}
