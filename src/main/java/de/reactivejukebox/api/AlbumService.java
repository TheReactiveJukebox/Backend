package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.database.Database;
import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.logger.AlbumFeedbackEntry;
import de.reactivejukebox.logger.LoggerProvider;
import de.reactivejukebox.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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

    @GET
    @Path("/feedback")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeedback(@QueryParam("id") List<Integer> id, @Context User user) {
        try {
            return Response.status(200).entity(Model.getInstance().getSpecialFeedbacks().getAlbumFeedback(id, user.getId())).build();
        } catch (Exception e) {
            System.err.println("Error getting album feedback for album " + id + ":");
            e.printStackTrace();
            return Response.status(400).build();
        }
    }

    @POST
    @Path("/feedback")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFeedback(AlbumFeedback feedback, @Context User user) {
        try {
            AlbumFeedback feedbackReturn = Model.getInstance()
                    .getSpecialFeedbacks()
                    .putAlbumFeedback(feedback, user.getId());
            LoggerProvider.getLogger().writeEntry(new AlbumFeedbackEntry(user, feedbackReturn));
            return Response.status(200).entity(feedbackReturn).build();
        } catch (Exception e) {
            System.err.println("Error adding album feedback for album " + feedback.getAlbum() + ":");
            e.printStackTrace();
            return Response.status(400).build();
        }
    }
}
