package de.reactivejukebox.core;

import de.reactivejukebox.database.PreparedStatementBuilder;
import de.reactivejukebox.model.Album;
import de.reactivejukebox.model.Artist;
import de.reactivejukebox.model.MusicEntity;
import de.reactivejukebox.model.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for finding tracks, artists and albums in the database.
 * <p>
 * Use one of the factory methods and provide your search parameters to create a tailored
 * Search object, on which you can call the execute() Method to run the query.
 */
public class Search {

    protected enum For {
        Album,
        Artist,
        Track
    }

    protected PreparedStatementBuilder stmnt;
    protected Connection con;
    protected List<MusicEntity> result = null;
    protected For musicObject;

    protected Search(For musicObject, PreparedStatementBuilder stmnt) {
        this.musicObject = musicObject;
        this.stmnt = stmnt;
    }

    /**
     * Execute the search.
     *
     * @param resultCount optional parameter to limit the number of results. Set to 0 for all results
     * @return A list of MusicEntity objects (Album, Artist, Track) matching the search parameters
     * @throws SQLException when an SQLException is thrown during statement execution or result set parsing
     */
    public List<MusicEntity> execute(int resultCount) throws SQLException {
        if (stmnt == null) {
            // if stmnt is not set, return empty list
            return new ArrayList<>();
        }
        con = Database.getInstance().getConnection();
        PreparedStatement dbQuery = stmnt.prepare(con);
        if (result == null) {
            ResultSet rs = dbQuery.executeQuery();
            ArrayList<MusicEntity> results = new ArrayList<>();

            // set count to max if parameter not present
            if (resultCount == 0) {
                resultCount = Integer.MAX_VALUE;
            }

            // find out what we're looking for and parse dbQuery result accordingly
            if (musicObject == For.Track) {
                while (resultCount-- > 0 && rs.next()) {
                    Track t = new Track(
                            rs.getInt(rs.findColumn("id")),
                            rs.getString(rs.findColumn("title")),
                            rs.getString(rs.findColumn("name")),
                            rs.getString(rs.findColumn("albumtitle")),
                            rs.getString(rs.findColumn("cover")),
                            rs.getString(rs.findColumn("hash")),
                            rs.getInt(rs.findColumn("duration"))
                    );
                    results.add(t);
                }
            } else if (musicObject == For.Album) {
                while (resultCount-- > 0 && rs.next()) {
                    Album a = new Album();
                    a.setId(rs.getInt(rs.findColumn("id")));
                    a.setArtist(rs.getString(rs.findColumn("name")));
                    a.setTitle(rs.getString(rs.findColumn("title")));
                    results.add(a);
                }
            } else if (musicObject == For.Artist) {
                while (resultCount-- > 0 && rs.next()) {
                    Artist a = new Artist();
                    a.setId(rs.getInt(rs.findColumn("id")));
                    a.setName(rs.getString(rs.findColumn("name")));
                    results.add(a);
                }
            }
            rs.close();
            con.close();

            this.result = results;
        }
        return result;
    }

    /**
     * Factory method for track searches.
     *
     * @param database       the Database Singleton
     * @param id             optional track id, set to 0 to disable filter
     * @param titleSubstring optional song title substring, set to null to disable filter
     * @param artist         optional artist id, set to 0 to disable filter
     * @return the resulting Search object that allows the execution of the query.
     */
    public static Search forTrack(Database database, int id, String titleSubstring, int artist) {
        PreparedStatementBuilder builder = new PreparedStatementBuilder();
        builder.select("song.id, song.title, artist.name, album.title as albumtitle, album.cover, song.hash, song.duration")
                .from("song, song_artist, artist, album")
                .addFilter("song.albumid=album.id")
                .addFilter("song.id=song_artist.songid")
                .addFilter("song_artist.artistid = artist.id");
        if (id != 0) {
            builder.addFilter("song.id=?", (query, i) -> query.setInt(i, id));
        } else {
            if (titleSubstring != null) {
                builder.addFilter("song.titlenormalized LIKE ?",
                        (query, i) -> query.setString(i, database.normalize(titleSubstring) + "%"));
            }
            if (artist != 0) {
                builder.addFilter("artist.id=?", (query, i) -> query.setInt(i, artist));
            }
        }
        return new Search(For.Track, builder);
    }

    /**
     * Factory method for album searches.
     *
     * @param database       the Database Singleton
     * @param id             optional album id, set to 0 to disable filter
     * @param titleSubstring optional album title substring, set to null to disable filter
     * @param artist         optional artist id, set to 0 to disable filter
     * @return the resulting Search object that allows the execution of the query.
     */
    public static Search forAlbum(Database database, int id, String titleSubstring, int artist) {
        PreparedStatementBuilder builder = new PreparedStatementBuilder()
                .select("album.id, album.title, artist.name")
                .from("album, artist, album_artist")
                .addFilter("album.id=album_artist.albumid")
                .addFilter("artist.id=album_artist.artistid");
        if (id != 0) {
            builder.addFilter("album.id=?", (query, i) -> query.setInt(i, id));
        } else {
            if (titleSubstring != null) {
                builder.addFilter("titlenormalized LIKE ?",
                        (query, i) -> query.setString(i, database.normalize(titleSubstring) + "%"));
            }
            if (artist != 0) {
                builder.addFilter("artist.id=?", (query, i) -> query.setInt(i, artist));
            }
        }
        return new Search(For.Album, builder);

    }

    /**
     * Factory method for artist searches.
     *
     * @param database the Database Singleton
     * @param id       optional track id, set to 0 to disable filter
     * @return the resulting Search object that allows the execution of the query.
     */
    public static Search forArtist(Database database, int id, String nameSubstring) {
        PreparedStatementBuilder builder = new PreparedStatementBuilder().select("*").from("artist");
        if (id != 0) {
            builder.addFilter("id=?", (query, i) -> query.setInt(i, id));
        } else if (nameSubstring != null) {
            builder.addFilter("NameNormalized LIKE ?",
                    (query, i) -> query.setString(i, database.normalize(nameSubstring) + "%"));
        }
        return new Search(For.Artist, builder);
    }


}
