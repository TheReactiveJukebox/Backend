package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.datahandlers.RadioHandler;
import de.reactivejukebox.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/jukebox")
public class JukeboxService {

    private static final String QUERY_CREATE_NEW_RADIOSTATION =
            "INSERT INTO radio (userid, israndom) VALUES (?, ?);";

    private static final String QUERY_RADIOSTATION_BY_USER_ID =
            "SELECT * FROM radio WHERE userid = ? ORDER BY id DESC LIMIT 1;";

    private static final String QUERY_RANDOM_RADIOSTATION =
            "SELECT " +
            "  song.Id              AS SongId, " +
            "  song.Title           AS SongTitle, " +
            "  song.Duration        AS SongDuration, " +
            "  song.Hash            AS SongHash, " +
            "  artist.name          AS Artists, " +
            "  album.Id             AS AlbumId, " +
            "  album.Title          AS AlbumTitle, " +
            "  album.Cover          AS AlbumCover, " +
            "  song_artist.artistid AS ArtistID " +
            "FROM song " +
            "  LEFT JOIN song_artist ON song.id=song_artist.songid " +
            "  LEFT JOIN artist ON artist.id=song_artist.artistid " +
            "  LEFT JOIN album ON album.id=song.albumid " +
            "WHERE NOT EXISTS(SELECT * " +
            "                 FROM history " +
            "                 WHERE song.id = songid AND userid = ?) " +
            "GROUP BY song.id, song.title, song.duration, song.hash, song_artist.artistid, " +
            "  artist.name, album.id, album.title, album.cover " +
            "ORDER BY RANDOM() " +
            "LIMIT ?";

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getCurrentRadiostation(@Context User user) {

        try {
            RadioPlain radio = new RadioHandler().getRadiostation(user);
            return Response.status(200)
                    .entity(radio)
                    .build();
        }catch(SQLException e){
            return Response.status(503)
                    .entity("Error no Radiostation available")
                    .build();
        }
    }

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createJukebox(RadioPlain r, @Context User user) {
        try {
            RadioPlain radio = new RadioHandler().addRadiostation(r,user);
            return Response.status(200)
                    .entity(radio)
                    .build();
            } catch (SQLException e) {
            return Response.status(503)
                    .entity("Error while writing/reading database")
                    .build();
        }
    }

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/next")
    public Response getNextSongs(@Context User user, @QueryParam("count") int count) {
        try {
            List<Track> results = new RadioHandler().getSongs(count, user);
            return Response.status(200)
                    .entity(results)
                    .build();
        } catch (SQLException e) {
            return Response.status(502)
                    .entity("Error while communicating with database.")
                    .build();
        }
    }
}
