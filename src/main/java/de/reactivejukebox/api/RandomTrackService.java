package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.Track;

import de.reactivejukebox.core.Database;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.text.SimpleDateFormat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;


@Path("/randtrack")
public class RandomTrackService {
    private PreparedStatement selectRandSong;


    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/{count}")
    public Track[] getTrackList(@PathParam("count") int count)  throws SQLException {

        // trigger loading the JDBC Driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            e.printStackTrace();//will not happen since the driver is a working dependency
        }
        try {
            /* create connection and prepare statements. Note that the Connection is never closed.
             * This is because the connection is held until the server is shut down.
             */
            Connection con = Database.getInstance().getConnection();
            selectRandSong = con.prepareStatement(
                    "SELECT song.Id AS SongId, song.Title AS SongTitle, song.Duration AS SongDuration, song.Hash, array_agg(artist.Name) AS Artists, album.Id AS AlbumId, album.Title AS AlbumTitle, album.Cover AS AlbumCover FROM (((song LEFT JOIN song_artist ON ((song.id = song_artist.songid))) LEFT JOIN artist ON ((artist.id = song_artist.artistid))) LEFT JOIN album ON ((album.id = song.albumid))) GROUP BY song.id, song.title, song.duration, song.hash, album.id, album.title, album.cover ORDER BY RANDOM() LIMIT 1;");
        } catch (SQLException e) {
            throw new RuntimeException("could not establish connection to Database please restart or contact developer!");
        }


        Track dbRandTrack = getRandomTrackFromDB();


        Track[] trackarray = {
                new Track(2342, "Never Gonna Give You Up", "Rick Astley", "Whenever You Need Somebody", "https://lastfm-img2.akamaized.net/i/u/ar0/66055acdca0f5b29f2d89e11d837eed5", 546),
                new Track(dbRandTrack.getId(), dbRandTrack.getTitle(), dbRandTrack.getArtist(), dbRandTrack.getAlbum(), dbRandTrack.getCover() , dbRandTrack.getDuration()),
                new Track(23, "Hells Bells", "AC/DC", "Back in Black", "https://upload.wikimedia.org/wikipedia/en/2/23/HellsBells.jpg", 312),
                new Track(84, "Paint it Black", "The Rolling Stones", "Singles 1965-1967", "http://www.covermesongs.com/wp-content/uploads/2010/09/PaintItBlack-400x400.jpg", 224),
                new Track(424, "Wonderwall", "Oasis", "[What's the Story] Morning Glory?", "https://upload.wikimedia.org/wikipedia/en/1/17/Wonderwall_cover.jpg", 258),
                new Track(2, "Evil Ways", "Santana", "Santana (Legacy Edition)", "https://upload.wikimedia.org/wikipedia/en/8/84/Santana_-_Santana_%281969%29.png", 238),
                new Track(91, "Sweet Child O' Mine", "Guns N' Roses", "Greatest Hits", "https://upload.wikimedia.org/wikipedia/en/1/15/Guns_N%27_Roses_-_Sweet_Child_o%27_Mine.png", 355),
                new Track(153, "Come As You Are", "Nirvana", "Nevermind (Deluxe Edition)", "http://e.snmc.io/lk/f/l/e3aee4167b67150f78c352a0e4d129e5/3736618.jpg", 218),
                new Track(287, "Bodies", "Drowning Pool", "Sinner", "https://upload.wikimedia.org/wikipedia/en/4/49/Drowning_Pool-Bodies_CD_Cover.jpg", 201),
                new Track(42, "Crocodile Rock", "Elton John", "Don't Shooot Me I'm Only The Piano Player", "https://upload.wikimedia.org/wikipedia/en/0/0b/Elton_John_Crocodile_Rock_%282%29.jpg", 235),
                new Track(99, "I Was Made For Loving You", "KISS", "Dynasty (Remastered Version)", "http://streamd.hitparade.ch/cdimages/kiss-i_was_made_for_lovin_you_s.jpg", 217),
                new Track(168, "Come Out and Play", "The Offspring", "Smash", "https://upload.wikimedia.org/wikipedia/en/8/80/TheOffspringSmashalbumcover.jpg", 197),
                new Track(94, "Rock & Roll Queen", "The Subways", "Young For Eternity", "https://images-na.ssl-images-amazon.com/images/I/41T42C5YFEL.jpg", 169)
        };

        return Arrays.copyOfRange(trackarray, 0, count);
    }

    public Track getRandomTrackFromDB() throws SQLException {
        ResultSet rs = selectRandSong.executeQuery();
        Track dbRTrack;
        if (rs.next()) {
            //directly fill TrackData because there can only be one row since usernames are unique

            dbRTrack = new Track(
                    rs.getInt("SongId"),
                    rs.getString("SongTitle"),
                    rs.getString("Artists"),
                    rs.getString("AlbumTitle"),
                    rs.getString("AlbumCover"),
                    rs.getInt("SongDuration"));
        } else {
            throw new SQLException();
        }
        return dbRTrack;

    }
}