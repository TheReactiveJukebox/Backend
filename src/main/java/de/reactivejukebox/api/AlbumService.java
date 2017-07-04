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
public class AlbumService {

    @GET
    @Path("/album")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbum(
            @QueryParam("id") int albumid,
            @QueryParam("titlesubstr") String titleSubstring,
            @QueryParam("byartist") int artist,
            @QueryParam("count") int resultCount) {
        List<MusicEntity> result = new ArrayList<>();
        try {
            result = Search.forAlbum(Database.getInstance(), albumid, titleSubstring, artist).execute(resultCount);
        } catch (SQLException e) {
            // TODO implement error handling and logging
            e.printStackTrace();
        }
        return Response.status(200)
                .entity(result)
                .build();
    }
}
