package de.reactivejukebox.api;

import de.reactivejukebox.core.Database;
import de.reactivejukebox.model.Artist;
import de.reactivejukebox.model.Track;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
public class TrackService {

    private static final int MAX_RESULT_SIZE = 200;
    private static final String QUERY_TITLE_LIKE = "SELECT * FROM Song, songview WHERE titleNormalized LIKE ? AND Song.id=songview.songid";
    private static final String QUERY_SONG_BY_ID = "SELECT * FROM songview WHERE songid=?";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/track")
    public Response search(@QueryParam("id") int trackId,
                           @QueryParam("titlesubstr") String titleSubstring,
                           @QueryParam("count") int countResults) {
        PreparedStatement query;
        ArrayList<Track> results = new ArrayList<>();

        queryDB:
        try (Connection con = Database.getInstance().getConnection()) {
            if (trackId != 0) {
                // track id is unique, omit other parameters
                query = con.prepareStatement(QUERY_SONG_BY_ID);
                query.setInt(1, trackId);
            } else if (!"".equals(titleSubstring)) {
                //Database.getInstance().normalize(titleSubstring);
                query = con.prepareStatement(QUERY_TITLE_LIKE);
                query.setString(1, titleSubstring);
            } else {
                // no parameters specified: empty result, skip querying the database
                break queryDB;
            }

            ResultSet rs = query.executeQuery();

            // set count to max if parameter not present
            if (countResults == 0) {
                countResults = MAX_RESULT_SIZE;
            }

            // parse query result
            while (countResults-- > 0 && rs.next()) {
                Artist dummyArtist = new Artist();
                dummyArtist.setName(rs.getString(rs.findColumn("artists")));
                Track t = new Track(
                        rs.getInt(rs.findColumn("songid")),
                        rs.getString(rs.findColumn("title")),
                        dummyArtist,
                        rs.getString(rs.findColumn("albumtitle")),
                        rs.getString(rs.findColumn("cover")),
                        rs.getInt(rs.findColumn("duration")),
                        rs.getString(rs.findColumn("hash"))
                );
                results.add(t);
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