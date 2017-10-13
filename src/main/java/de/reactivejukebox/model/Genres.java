package de.reactivejukebox.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Genres is a Class that contains the mapping information From Frontend to Backend/Database Genres.
 */
public class Genres implements Iterable<String> {
    private static String SQL_QUERY = "SELECT g.name as metagenre, genre.name FROM genre JOIN genre g ON genre.metagenreid = g.id";
    private ConcurrentHashMap<String, String> genreToMetagenre;
    private ConcurrentHashMap<String, ArrayList<String>> metagenreToGenre;

    public Genres() {
        genreToMetagenre = new ConcurrentHashMap<>();
        metagenreToGenre = new ConcurrentHashMap<>();
    }

    public Genres(Connection con) throws SQLException {
        genreToMetagenre = new ConcurrentHashMap<>();
        metagenreToGenre = new ConcurrentHashMap<>();
        PreparedStatement stmnt = con.prepareStatement(SQL_QUERY);
        ResultSet rs = stmnt.executeQuery();
        String genre;
        String metagenre;
        while (rs.next()) {
            genre = rs.getString("name");
            metagenre = rs.getString("metagenre");
            genreToMetagenre.putIfAbsent(genre, metagenre);
            if (!metagenreToGenre.containsKey(metagenre)) {
                ArrayList<String> metalist = new ArrayList<>();
                metalist.add(metagenre);
                metagenreToGenre.putIfAbsent(metagenre, metalist);
            }
            metagenreToGenre.get(metagenre).add(genre);
        }
    }

    /**
     * @param genreName Name of a Genre
     * @return Name of the matching Metagenre
     */
    public String getMetaGenre(String genreName) {
        return genreToMetagenre.get(genreName);
    }

    /**
     * @param metaGenreName Name of a Metagenre
     * @return Name of all Matchign Genres
     */
    public ArrayList<String> getGenre(String metaGenreName) {
        return metagenreToGenre.get(metaGenreName);
    }

    /**
     * @return List of all Metagenres
     */
    public List<String> metaList() {
        Enumeration<String> e = metagenreToGenre.keys();
        return Collections.list(e);
    }

    /**
     * @return List of all Genres
     */
    public List<String> genreList() {
        Enumeration<String> e = genreToMetagenre.keys();
        return Collections.list(e);
    }

    /**
     * @param genre     Name of the new Genre
     * @param metagenre Name of the new Metagenre
     */
    public void put(String genre, String metagenre) {
        if (!genreToMetagenre.containsKey(genre)) {
            genreToMetagenre.putIfAbsent(genre, metagenre);
        }
        if (!metagenreToGenre.containsKey(metagenre)) {
            ArrayList<String> metalist = new ArrayList<>();
            metalist.add(metagenre);
            metagenreToGenre.putIfAbsent(metagenre, metalist);
        }
        metagenreToGenre.get(metagenre).add(genre);
    }

    @Override
    public Iterator<String> iterator() {
        return genreToMetagenre.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super String> consumer) {
        genreToMetagenre.values().forEach(consumer);
    }

    @Override
    public Spliterator<String> spliterator() {
        return genreToMetagenre.values().spliterator();
    }

    public Stream<String> stream() {
        return StreamSupport.stream(spliterator(), false);
    }


}