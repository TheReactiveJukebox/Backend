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

    private static final String QUERY_CREATE_NEW_RADIOSTATION = "INSERT INTO \"radio\" (userid, israndom) VALUES (?, ?);";
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
            currentRadiostation = new Radio(rs.getInt("id"), rs.getBoolean("israndom"), null, null, null, null, 0, 0);
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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jukebox")
    public Response createJukebox(@Context UserData user, @QueryParam("israndom") boolean isRandom) {
        PreparedStatement query;
        Radio radiostation;
        ResultSet rs;

        queryDB:
        try (Connection con = Database.getInstance().getConnection()) {

            query = con.prepareStatement(QUERY_CREATE_NEW_RADIOSTATION);
            query.setInt(1, user.getId());
            query.setBoolean(2, isRandom);
            query.executeQuery();

            query = con.prepareStatement(QUERY_RADIOSTATION_BY_USER_ID);
            query.setInt(1, user.getId());
            rs = query.executeQuery();
            radiostation = new Radio(rs.getInt("id"), isRandom, null, null, null, null, 0, 0);
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
            currentRadiostation = new Radio(rs.getInt("id"), rs.getBoolean("israndom"), null, null, null, null, 0, 0);
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
