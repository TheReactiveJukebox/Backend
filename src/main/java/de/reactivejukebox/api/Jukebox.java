package de.reactivejukebox.api;

import de.reactivejukebox.core.Database;
import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.user.UserData;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("/")
public class Jukebox {

    private static final String QUERY_CREATE_NEW_RADIOSTATION = "INSERT INTO radio (userid, israndom) VALUES (1, true);";
    private static final String QUERY_RADIOSTATION_BY_USER_ID = "SELECT * FROM radio WHERE userid = ? ORDER BY id DESC LIMIT 1;";
    private static final String QUERY_RANDOM_RADIOSTATION = "SELECT song.Id AS SongId, song.Title AS SongTitle, song.Duration AS SongDuration, song.Hash AS SongHash, array_agg(artist.Name) AS Artists, album.Id AS AlbumId, album.Title AS AlbumTitle, album.Cover AS AlbumCover FROM (((song LEFT JOIN song_artist ON ((song.id = song_artist.songid))) LEFT JOIN artist ON ((artist.id = song_artist.artistid))) LEFT JOIN album ON ((album.id = song.albumid))) WHERE NOT EXISTS  (SELECT * FROM history WHERE song.id = songid AND ? = userid) GROUP BY song.id, song.title, song.duration, song.hash, album.id, album.title, album.cover ORDER BY RANDOM() LIMIT ?;";


    /*
    Get the newest radiostation from current user
    Response is the Radio Object with hole specification
     */
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jukebox")
    public Response getCurrentRadiostation(@Context UserData user) {
        PreparedStatement query;
        Radio currentRadiostation;
        ResultSet rs;

        queryDB:
        try (Connection con = Database.getInstance().getConnection()) {
            query = con.prepareStatement(QUERY_RADIOSTATION_BY_USER_ID);
            query.setInt(1, user.getId());
            rs = query.executeQuery();
            currentRadiostation = new Radio(rs.getInt("id"), null, null, null, 0, 0, rs.getBoolean("israndom"));
            con.close();
        } catch (SQLException e) {
            // TODO encapsulate and improve error handling
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            sw.append(e.getMessage());
            e.printStackTrace(pw);
            return Response.status(502)
                    .entity("Error while communicating with database: " + sw.toString())
                    .build();
        }


        return Response.status(200)
                .entity(currentRadiostation)
                .build();
    }

    /*
    Creates a new Radiostation in DB, in response is a hole Radio Object with all parameters from DB
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jukebox")
    public Response createJukebox(Radio r, @Context UserData user) {


        StringWriter sw1 = new StringWriter();
        PrintWriter pw1 = new PrintWriter(sw1);
        sw1.append(r.toString());
        System.out.println("Jukebox.createJukebox: " + r.toString());

        PreparedStatement query;
        Radio radiostation;
        ResultSet rs;

        queryDB:
        try (Connection con = Database.getInstance().getConnection()) {

            query = con.prepareStatement(QUERY_CREATE_NEW_RADIOSTATION);
            query.setInt(1, user.getId());
            query.setBoolean(2, r.isRandom());
            query.executeQuery();

            query = con.prepareStatement(QUERY_RADIOSTATION_BY_USER_ID);
            query.setInt(1, user.getId());
            rs = query.executeQuery();
            radiostation = new Radio(rs.getInt("id"), null, null, null, 0, 0, r.isRandom());
            con.close();
        } catch (SQLException e) {
            // TODO encapsulate and improve error handling
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            sw.append(e.getMessage());
            e.printStackTrace(pw);
            return Response.status(502)
                    .entity("Error while communicating with database: " + sw.toString())
                    .build();
        }


        return Response.status(200)
                .entity(radiostation)
                .build();

    }

    /*
    Fetches current users newest radiostation and get next (parameter) count songs for this station
    Response is an ArrayList of Tracks
    */
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jukebox/next")
    public Response getNextSongs(@Context UserData user, @QueryParam("count") int count) {
        PreparedStatement query;
        Radio currentRadiostation;
        ResultSet rs;
        ArrayList<Track> results = new ArrayList<>();

        queryDB:
        try (Connection con = Database.getInstance().getConnection()) {
            query = con.prepareStatement(QUERY_RADIOSTATION_BY_USER_ID);
            query.setInt(1, user.getId());
            rs = query.executeQuery();
            currentRadiostation = new Radio(rs.getInt("id"), null, null, null, 0, 0, rs.getBoolean("israndom"));
            con.close();

            if (currentRadiostation.isRandom()) {
                query = con.prepareStatement(QUERY_RANDOM_RADIOSTATION);
                query.setInt(1, user.getId());
                query.setInt(2, count);
                rs = query.executeQuery();
                while (rs.next()) {
                    results.add(new Track(
                            rs.getInt(rs.findColumn("SongId")),
                            rs.getString(rs.findColumn("SongTitle")),
                            rs.getString(rs.findColumn("Artists")),
                            rs.getString(rs.findColumn("AlbumTitle")),
                            rs.getString(rs.findColumn("AlbumCover")),
                            rs.getInt(rs.findColumn("SongDuration")),
                            rs.getString(rs.findColumn("SongHash"))));
                }
            }
            //dummy result set
            if (results.isEmpty()) {
                results.add(new Track(2342, "Never Gonna Give You Up", "Rick Astley", "Whenever You Need Somebody", "https://lastfm-img2.akamaized.net/i/u/ar0/66055acdca0f5b29f2d89e11d837eed5", 546, "dummyhash"));
                results.add(new Track(84, "Paint it Black", "The Rolling Stones", "Singles 1965-1967", "http://www.covermesongs.com/wp-content/uploads/2010/09/PaintItBlack-400x400.jpg", 224, "dummyhash"));
                results.add(new Track(424, "Wonderwall", "Oasis", "[What's the Story] Morning Glory?", "https://upload.wikimedia.org/wikipedia/en/1/17/Wonderwall_cover.jpg", 258, "dummyhash"));
                results.add(new Track(2, "Evil Ways", "Santana", "Santana (Legacy Edition)", "https://upload.wikimedia.org/wikipedia/en/8/84/Santana_-_Santana_%281969%29.png", 238, "dummyhash"));
                results.add(new Track(91, "Sweet Child O' Mine", "Guns N' Roses", "Greatest Hits", "https://upload.wikimedia.org/wikipedia/en/1/15/Guns_N%27_Roses_-_Sweet_Child_o%27_Mine.png", 355, "dummyhash"));
                results.add(new Track(153, "Come As You Are", "Nirvana", "Nevermind (Deluxe Edition)", "http://e.snmc.io/lk/f/l/e3aee4167b67150f78c352a0e4d129e5/3736618.jpg", 218, "dummyhash"));
                results.add(new Track(287, "Bodies", "Drowning Pool", "Sinner", "https://upload.wikimedia.org/wikipedia/en/4/49/Drowning_Pool-Bodies_CD_Cover.jpg", 201, "dummyhash"));
                results.add(new Track(42, "Crocodile Rock", "Elton John", "Don't Shooot Me I'm Only The Piano Player", "https://upload.wikimedia.org/wikipedia/en/0/0b/Elton_John_Crocodile_Rock_%282%29.jpg", 235, "dummyhash"));
                results.add(new Track(99, "I Was Made For Loving You", "KISS", "Dynasty (Remastered Version)", "http://streamd.hitparade.ch/cdimages/kiss-i_was_made_for_lovin_you_s.jpg", 217, "dummyhash"));
                results.add(new Track(168, "Come Out and Play", "The Offspring", "Smash", "https://upload.wikimedia.org/wikipedia/en/8/80/TheOffspringSmashalbumcover.jpg", 197, "dummyhash"));
                results.add(new Track(94, "Rock & Roll Queen", "The Subways", "Young For Eternity", "https://images-na.ssl-images-amazon.com/images/I/41T42C5YFEL.jpg", 169, "dummyhash"));
            }

        } catch (SQLException e) {
            // TODO encapsulate and improve error handling
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            sw.append(e.getMessage());
            e.printStackTrace(pw);
            return Response.status(502)
                    .entity("Error while communicating with database: " + sw.toString())
                    .build();
        }


        return Response.status(200)
                .entity(results)
                .build();
    }
}
