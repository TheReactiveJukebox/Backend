package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;

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
            "SELECT song.id, song.title, song_artist.artistid, song.albumid, album.cover, song.hash, song.duration, " +
                    "song.playcount, song.published, song.bpm, song.dynamics, song.spotifyid, song.spotifyurl, song.mirarousal, song.mirvalence " +
                    "FROM song, song_artist, album " +
                    "WHERE song.albumid=album.id " +
                    "      AND song.id=song_artist.songid";

    private static final String SQL_GENRE =
            "SELECT genre.name FROM genre, song_genre WHERE song_genre.songid  = ? " +
                    "AND song_genre.genreid = genre.id";

    private static final String SQL_OLD =
            "SELECT MIN(published) AS published FROM song WHERE published >= '1000-01-01';";

    private static final String SQL_MINSPEED =
            "SELECT MIN(bpm) AS bpm FROM song WHERE bpm > 1;";

    private static final String SQL_MAXSPEED =
            "SELECT MAX(bpm) AS bpm FROM song WHERE bpm > 1;";


    protected Map<Integer, Track> tracks;
    protected Map<String, Track> tracksBySpotifyId;

    public Tracks() {
        tracks = new ConcurrentHashMap<>();
        tracksBySpotifyId = new ConcurrentHashMap<>();
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

            String spotifyId = rs.getString("spotifyid");

            int fSpeed = Math.round(rs.getFloat("bpm")/5);
            int fMood = new MoodKey(rs.getFloat("mirarousal"),rs.getFloat("mirvalence")).hashCode();

            float valence = Math.max(-1,Math.min(rs.getFloat("mirvalence"),1));
            float arousal = Math.max(-1,Math.min(rs.getFloat("mirarousal"),1));

            Track t =  new Track(
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
                    rs.getFloat("dynamics"),
                    spotifyId,
                    rs.getString("spotifyurl"),
                    valence,
                    arousal,
                    fSpeed,
                    fMood

            );
            tracks.put(id, t);
            tracksBySpotifyId.put(spotifyId, t);
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

    public Track getBySpotifyId(String id) {
        return tracksBySpotifyId.get(id);
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

    public TrackParameter getTrackParameter() throws SQLException {
        TrackParameter trackParameter = new TrackParameter();
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement stmnt = con.prepareStatement(SQL_OLD);
        ResultSet rs = stmnt.executeQuery();
        Calendar newCalendar = new GregorianCalendar();
        if (rs.next()) {
            java.sql.Date newDate = rs.getDate("published");
            // check, if the track has a published date and set it, if present. If we have no date available, we just set null.
            if (newDate != null) {
                newCalendar.setTimeInMillis(newDate.getTime());
            }
        }
        trackParameter.setOldestTrack(newCalendar.get(Calendar.YEAR));

        stmnt = con.prepareStatement(SQL_MINSPEED);
        rs = stmnt.executeQuery();
        if (rs.next()) {
            trackParameter.setMinSpeed(rs.getFloat("bpm"));
        }
        stmnt = con.prepareStatement(SQL_MAXSPEED);
        rs = stmnt.executeQuery();
        if (rs.next()) {
            trackParameter.setMaxSpeed(rs.getFloat("bpm"));
        }

        con.close();
        return trackParameter;

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
