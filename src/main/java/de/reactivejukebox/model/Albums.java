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

public class Albums implements Iterable<Album> {

    private static final String SQL_QUERY =
            "SELECT id, title, artistid " +
                    "FROM album, album_artist " +
                    "WHERE album.id=album_artist.albumid";

    protected Map<Integer, Album> albums;

    public Albums() {
        albums = new ConcurrentHashMap<>();
    }

    public Albums(Connection con, Artists artists) throws SQLException {
        this();
        PreparedStatement stmnt = con.prepareStatement(SQL_QUERY);
        ResultSet rs = stmnt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            albums.put(id, new Album(
                    id,
                    rs.getString("title"),
                    artists.get(rs.getInt("artistid"))
            ));
        }
    }

    public Album get(int id) {
        return albums.get(id);
    }

    public Album put(int id, Album album) {
        return albums.put(id, album);
    }

    public Album remove(int id) {
        return albums.remove(id);
    }

    @Override
    public Iterator<Album> iterator() {
        return albums.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super Album> consumer) {
        albums.values().forEach(consumer);
    }

    @Override
    public Spliterator<Album> spliterator() {
        return albums.values().spliterator();
    }

    public Stream<Album> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}

