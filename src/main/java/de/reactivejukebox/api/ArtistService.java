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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/artist")
public class ArtistService {

    @GET
    @Path("/")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArtist(
            @QueryParam("id") List<Integer> id,
            @QueryParam("namesubstr") String nameSubstring,
            @QueryParam("count") int count) {
        Set<Integer> ids = new TreeSet<>(id);
        List<MusicEntityPlain> result;
        Stream<Artist> s = Model.getInstance().getArtists().stream();
        if (!ids.isEmpty()) {
            s = s.filter(artist -> ids.contains(artist.getId()));
        }
        if (nameSubstring != null) {
            Database db = DatabaseProvider.getInstance().getDatabase();
            s = s.filter(artist ->
                    db.normalize(artist.getName()).startsWith(db.normalize(nameSubstring)));
        }
        result = s.map(Artist::getPlainObject).collect(Collectors.toList());
        return Response.status(200)
                .entity(result)
                .build();
    }
}