package de.reactivejukebox.api;

import de.reactivejukebox.core.Database;
import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.Artist;
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
    private static final String QUERY_RANDOM_RADIOSTATION = "SELECT song.Id AS SongId, song.Title AS SongTitle, song.Duration AS SongDuration, song.Hash AS SongHash, artist.name AS Artists, album.Id AS AlbumId, album.Title AS AlbumTitle, album.Cover AS AlbumCover, song_artist.artistid AS ArtistID FROM (((song LEFT JOIN song_artist ON ((song.id = song_artist.songid))) LEFT JOIN artist ON ((artist.id = song_artist.artistid))) LEFT JOIN album ON ((album.id = song.albumid))) WHERE NOT EXISTS  (SELECT * FROM history WHERE song.id = songid AND ? = userid) GROUP BY song.id, song.title, song.duration, song.hash, artist.name, album.id, album.title, album.cover, song_artist.artistid ORDER BY RANDOM() LIMIT ?;";


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
            if (rs.next()) {
                currentRadiostation = new Radio(rs.getInt("id"), null, null, null, 0, 0, rs.getBoolean("israndom"));
            } else {
                return Response.status(503)
                        .entity("Error no Radiostation available")
                        .build();
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(502)
                    .entity("Error while communicating with database.")
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
            query.executeUpdate();

            query = con.prepareStatement(QUERY_RADIOSTATION_BY_USER_ID);
            query.setInt(1, user.getId());
            rs = query.executeQuery();
            if (rs.next()) {
                radiostation = new Radio(rs.getInt("id"), null, null, null, 0, 0, r.isRandom());
            } else {
                radiostation = new Radio();
                return Response.status(503)
                        .entity("Error while writing/reading to database")
                        .build();
            }


            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(502)
                    .entity("Error while communicating with database.")
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
            if (rs.next()) {
                currentRadiostation = new Radio(rs.getInt("id"), null, null, null, 0, 0, rs.getBoolean("israndom"));
            } else {
                return Response.status(503)
                        .entity("Error while writing/reading to database maybe current user hasn't an active Radiostation")
                        .build();
            }

            if (currentRadiostation.isRandom()) {
                query = con.prepareStatement(QUERY_RANDOM_RADIOSTATION);
                query.setInt(1, user.getId());
                query.setInt(2, count);
                rs = query.executeQuery();

                while (rs.next()) {
                    Artist tmpArtist = new Artist();
                    tmpArtist.setName(rs.getString(rs.findColumn("Artists")));
                    tmpArtist.setId(rs.getInt(rs.findColumn("ArtistID")));
                    results.add(new Track(
                            rs.getInt(rs.findColumn("SongId")),
                            rs.getString(rs.findColumn("SongTitle")),
                            tmpArtist,
                            rs.getString(rs.findColumn("AlbumTitle")),
                            rs.getString(rs.findColumn("AlbumCover")),
                            rs.getString(rs.findColumn("SongHash")),
                            rs.getInt(rs.findColumn("SongDuration"))
                    ));
                }
            }

            if (results.isEmpty()) {
                return Response.status(504)
                        .entity("Error ne next "+count+" songs are available")
                        .build();
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(502)
                    .entity("Error while communicating with database.")
                    .build();
        }


        return Response.ok(results)
                .build();
    }
}
