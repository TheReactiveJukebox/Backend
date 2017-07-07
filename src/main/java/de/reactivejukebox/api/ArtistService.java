package de.reactivejukebox.api;

import de.reactivejukebox.core.Search;
import de.reactivejukebox.core.Secured;
import de.reactivejukebox.database.DatabaseFactory;
import de.reactivejukebox.model.MusicEntity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/artist")
public class ArtistService {

    @GET
    @Path("/")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArtist(
            @QueryParam("id") int id,
            @QueryParam("namesubstr") String nameSubstring,
            @QueryParam("count") int count) {
        try {
            List<MusicEntity> results = Search.forArtist(DatabaseFactory.getInstance().getDatabase(), id, nameSubstring).execute(count);
            return Response.status(200)
                    .entity(results)
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity("An error occured while querying the database for artists.")
                    .build();
        }
    }
}