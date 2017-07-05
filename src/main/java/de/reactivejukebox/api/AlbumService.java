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

@Path("/album")
public class AlbumService {

    @GET
    @Path("/")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbum(
            @QueryParam("id") int albumid,
            @QueryParam("titlesubstr") String titleSubstring,
            @QueryParam("artist") int artist,
            @QueryParam("count") int resultCount) {
        try {
            List<MusicEntity> results = Search.forAlbum(Database.getInstance(), albumid, titleSubstring, artist).execute(resultCount);
            return Response.status(200)
                    .entity(results)
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity("An error occured while querying the database for albums.")
                    .build();
        }
    }
}
