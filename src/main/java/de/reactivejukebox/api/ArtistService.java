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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<MusicEntityPlain> result;
        if (id != 0) {
            result = new ArrayList<>();
            result.add(Model.getInstance().getArtists().get(id).getPlainObject());
        } else {
            Stream<Artist> s = Model.getInstance().getArtists().stream();
            if (nameSubstring != null) {
                Database db = DatabaseProvider.getInstance().getDatabase();
                s = s.filter(artist ->
                        db.normalize(artist.getName()).startsWith(db.normalize(nameSubstring)));
            }
            result = s.map(Artist::getPlainObject).collect(Collectors.toList());
        }
        return Response.status(200)
                .entity(result)
                .build();
    }
}