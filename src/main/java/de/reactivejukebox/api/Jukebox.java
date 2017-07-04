package de.reactivejukebox.api;

import de.reactivejukebox.core.Database;
import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.user.UserData;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
    private static final String QUERY_RADIOSTATION_BY_ID = "";
    private static final String QUERY_RANDOM_RADIOSTATION = "SELECT song.Id AS SongId, song.Title AS SongTitle, song.Duration AS SongDuration, song.Hash AS SongHash, array_agg(artist.Name) AS Artists, album.Id AS AlbumId, album.Title AS AlbumTitle, album.Cover AS AlbumCover FROM (((song LEFT JOIN song_artist ON ((song.id = song_artist.songid))) LEFT JOIN artist ON ((artist.id = song_artist.artistid))) LEFT JOIN album ON ((album.id = song.albumid))) WHERE NOT EXISTS  (SELECT * FROM history WHERE song.id = songid AND ? = userid) GROUP BY song.id, song.title, song.duration, song.hash, album.id, album.title, album.cover ORDER BY RANDOM() LIMIT ?;";
    private static final int INIT_SONG_COUNT = 6;

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jukebox")

    //@QueryParam boolean isRandom
    public Response createJukebox(@Context UserData user,  boolean isRandom){
        PreparedStatement query;
        Radio randiostation;
        ArrayList<Track> results = new ArrayList<>();

        queryDB:
        try (Connection con = Database.getInstance().getConnection()) {

            query = con.prepareStatement(QUERY_CREATE_NEW_RADIOSTATION);
            query.setInt(1, user.getId());
            query.setBoolean(2, isRandom);

            ResultSet rs = query.executeQuery();


            if(isRandom){
                query = con.prepareStatement(QUERY_RANDOM_RADIOSTATION);
                query.setInt(1, user.getId());
                query.setInt(2, INIT_SONG_COUNT);

                rs = query.executeQuery();
            }



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
                .entity(results)
                .build();

    }
}
