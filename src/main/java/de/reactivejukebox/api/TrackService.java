package de.reactivejukebox.api;

import de.reactivejukebox.core.Database;
import de.reactivejukebox.core.Search;
import de.reactivejukebox.model.MusicEntity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class TrackService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/track")
    public Response search(@QueryParam("id") int trackId,
                           @QueryParam("titlesubstr") String titleSubstring,
                           @QueryParam("artist") int artist,
                           @QueryParam("count") int countResults) {
        List<MusicEntity> results = new ArrayList<>();
        try {
             results = Search.forTrack(Database.getInstance(), trackId, titleSubstring, artist).execute(countResults);
        } catch (SQLException e) {
            // TODO implement error handling and logging
            e.printStackTrace();
        }
        return Response.status(200)
                .entity(results)
                .build();
    }
}