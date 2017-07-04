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
public class ArtistService {

    @GET
    @Path("/artist")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArtist(
            @QueryParam("id") int id,
            @QueryParam("namesubstr") String nameSubstring,
            @QueryParam("count") int count) {
        List<MusicEntity> results = new ArrayList<>();
        try {
            results = Search.forArtist(Database.getInstance(), id, nameSubstring).execute(count);
        } catch (SQLException e) {
            // TODO implement error handling and logging
            e.printStackTrace();
        }
        return Response.status(200)
                .entity(results)
                .build();
    }
}