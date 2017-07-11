package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.database.Database;
import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.model.Album;
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

@Path("/album")
public class AlbumService {

    @GET
    @Path("/")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbum(
            @QueryParam("id") List<Integer> id,
            @QueryParam("titlesubstr") String titleSubstring,
            @QueryParam("artist") int artist,
            @QueryParam("count") int resultCount) {
        List<MusicEntityPlain> results;
        Set<Integer> ids = new TreeSet<>(id);
        Stream<Album> s = Model.getInstance().getAlbums().stream();
        if (!id.isEmpty()) {
            s = s.filter(album -> ids.contains(album.getId()));
        }
        if (titleSubstring != null) {
            Database db = DatabaseProvider.getInstance().getDatabase();
            s = s.filter(album ->
                    db.normalize(album.getTitle()).startsWith(db.normalize(titleSubstring)));
        }
        if (artist != 0) {
            s = s.filter(album -> album.getArtist().getId() == artist);
        }
        results = s.map(Album::getPlainObject).collect(Collectors.toList());

        return Response.status(200)
                .entity(results)
                .build();
    }
}
