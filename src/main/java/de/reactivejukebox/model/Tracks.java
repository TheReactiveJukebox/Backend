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

public class Tracks implements Iterable<Track> {

    private static final String SQL_QUERY =
            "SELECT song.id, song.title, song_artist.artistid, song.albumid, album.cover, song.hash, song.duration " +
            "FROM song, song_artist, album " +
            "WHERE song.albumid=album.id " +
            "      AND song.id=song_artist.songid";
    protected Map<Integer, Track> tracks;
    List<Integer> keys;
    Random rng;

    public Tracks() {
        tracks = new ConcurrentHashMap<>();
        keys = new ArrayList<Integer>(tracks.keySet());
        rng = new Random();
    }

    public Tracks(Connection con, Artists artists, Albums albums) throws SQLException {
        this();
        PreparedStatement stmnt = con.prepareStatement(SQL_QUERY);
        ResultSet rs = stmnt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            tracks.put(id, new Track(
                    id,
                    rs.getString("title"),
                    artists.get(rs.getInt("artistid")),
                    albums.get(rs.getInt("albumid")),
                    rs.getString("cover"),
                    rs.getString("hash"),
                    rs.getInt("duration")
            ));
        }
        keys = new ArrayList<Integer>(tracks.keySet());
    }

    public Track get(int id) {
        return tracks.get(id);
    }

    //TODO improve performance
    public ArrayList<Track> getRandom(int count){
        ArrayList<Track> results = new ArrayList<>();
        for(int i =0;i<count;i++) {
            results.add(tracks.get(keys.get(rng.nextInt(keys.size()))));
        }
        return results;
    }

    // TODO if need be: write changes back to database
    public Track put(int id, Track track) {
        return tracks.put(id, track);
    }

    public Track remove(int id) {
        return tracks.remove(id);
    }

    @Override
    public Iterator<Track> iterator() {
        return tracks.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super Track> consumer) {
        tracks.values().forEach(consumer);
    }

    @Override
    public Spliterator<Track> spliterator() {
        return tracks.values().spliterator();
    }

    public Stream<Track> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
