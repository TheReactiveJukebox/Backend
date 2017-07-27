package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.database.Database;
import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.model.Artist;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.MusicEntityPlain;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/genre")
public class GenreService {
    @GET
    @Path("/list")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenreList() {
        List<String> result = Model.getInstance().getGenres().metaList();
        return Response.status(200)
                .entity(result)
                .build();
    }

}
