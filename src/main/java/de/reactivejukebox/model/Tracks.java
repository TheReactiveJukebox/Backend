package de.reactivejukebox.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Tracks implements Iterable<Track> {

    private static final String SQL_QUERY =
            "SELECT song.id, song.title, song_artist.artistid, song.albumid, album.cover, song.hash, song.duration, song.playcount, song.published, song.bpm, song.dynamics " +
                    "FROM song, song_artist, album " +
                    "WHERE song.albumid=album.id " +
                    "      AND song.id=song_artist.songid";

    private static final String SQL_GENRE =
            "SELECT genre.name FROM genre, song_genre WHERE song_genre.songid  = ? " +
                    "AND song_genre.genreid = genre.id";

    protected Map<Integer, Track> tracks;

    public Tracks() {
        tracks = new ConcurrentHashMap<>();
    }

    public Tracks(Connection con, Artists artists, Albums albums) throws SQLException {
        this();
        PreparedStatement stmnt = con.prepareStatement(SQL_QUERY);
        ResultSet rs = stmnt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            java.sql.Date newDate = rs.getDate("published");
            java.util.Date javaDate = null;
			// check, if the track has a published date and set it, if present. If we have no date available, we just set null.
            if (newDate != null) {
                javaDate = new Date(rs.getDate("published").getTime());
            }
            tracks.put(id, new Track(
                    id,
                    rs.getString("title"),
                    artists.get(rs.getInt("artistid")),
                    albums.get(rs.getInt("albumid")),
                    rs.getString("cover"),
                    rs.getString("hash"),
                    rs.getInt("duration"),
                    rs.getInt("playcount"),
                    javaDate,
                    rs.getFloat("bpm"),
                    rs.getFloat("dynamics")
            ));
        }
        for (Track t : this) {
            int id = t.getId();
            stmnt = con.prepareStatement(SQL_GENRE);
            stmnt.setInt(1, id);
            rs = stmnt.executeQuery();
            while (rs.next()) {
                t.getGenres().add(rs.getString("name"));
            }
        }
    }

    public Track get(int id) {
        return tracks.get(id);
    }

    public Track put(int id, Track track) {
        return tracks.put(id, track);
    }

    public Track remove(int id) {
        return tracks.remove(id);
    }

    public int size() {
        return tracks.size();
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
