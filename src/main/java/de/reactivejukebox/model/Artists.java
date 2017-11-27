package de.reactivejukebox.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Artists implements Iterable<Artist> {

    private static String SQL_QUERY = "SELECT * FROM artist";
    private Map<Integer, Artist> artists;

    public Artists() {
        artists = new ConcurrentHashMap<>();
    }

    public Artists(Connection con) throws SQLException {
        this();
        PreparedStatement stmnt = con.prepareStatement(SQL_QUERY);
        ResultSet rs = stmnt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            artists.put(id, new Artist(id, rs.getString("name")));
        }
        rs.close();
        stmnt.close();
    }

    @Override
    public Iterator<Artist> iterator() {
        return artists.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super Artist> consumer) {
        artists.values().forEach(consumer);
    }

    @Override
    public Spliterator<Artist> spliterator() {
        return artists.values().spliterator();
    }

    public Artist get(int id) {
        return artists.get(id);
    }

    public Artist put(int id, Artist a) {
        return artists.put(id, a);
    }

    public Artist remove(int id) {
        return artists.remove(id);
    }

    public Stream<Artist> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
