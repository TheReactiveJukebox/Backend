package de.reactivejukebox.api;

import de.reactivejukebox.core.Database;
import de.reactivejukebox.core.Search;
import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.MusicEntity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/track")
public class TrackService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/")
    public Response search(@QueryParam("id") int trackId,
                           @QueryParam("titlesubstr") String titleSubstring,
                           @QueryParam("artist") int artist,
                           @QueryParam("count") int countResults) {
        try {
            List<MusicEntity> results = Search.forTrack(Database.getInstance(), trackId, titleSubstring, artist).execute(countResults);
            return Response.status(200)
                    .entity(results)
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity("An error occured while querying the database for tracks.")
                    .build();
        }
    }
}