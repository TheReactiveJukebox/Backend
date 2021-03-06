package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.database.Database;
import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.logger.ArtistFeedbackEntry;
import de.reactivejukebox.logger.LoggerProvider;
import de.reactivejukebox.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
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
            @Context User user,
            @QueryParam("id") List<Integer> id,
            @QueryParam("namesubstr") String nameSubstring,
            @QueryParam("count") int count) {
        List<ArtistPlain> result = null;
        try {
            Set<Integer> ids = new TreeSet<>(id);
            Stream<Artist> s = Model.getInstance().getArtists().stream();
            if (!ids.isEmpty()) {
                s = s.filter(artist -> ids.contains(artist.getId()));
            }
            if (nameSubstring != null) {
                Database db = DatabaseProvider.getInstance().getDatabase();
                s = s.filter(artist ->
                        db.normalize(artist.getName()).contains(db.normalize(nameSubstring)));
            }
            result = s.map(Artist::getPlainObject).collect(Collectors.toList());
            SpecialFeedbacks feedback = Model.getInstance().getSpecialFeedbacks();
            for (ArtistPlain artist : result) {
                artist.setFeedback(feedback.getArtistFeedback(artist.getId(), user.getId()));
            }
            return Response.status(200)
                    .entity(result)
                    .build();
        } catch (SQLException e) {
            System.err.println("Error setting artist feedback into artist");
            e.printStackTrace();
            return Response.status(200)
                    .entity(result)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(501).entity("Internal Error").build();
        }
    }

    @GET
    @Path("/feedback")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeedback(@QueryParam("id") List<Integer> id, @Context User user) {
        try {
            return Response.status(200).entity(Model.getInstance().getSpecialFeedbacks().getArtistFeedback(id, user.getId())).build();
        } catch (Exception e) {
            System.err.println("Error getting artist feedback:");
            e.printStackTrace();
            return Response.status(400).build();
        }
    }

    @POST
    @Path("/feedback")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFeedback(ArtistFeedback feedback, @Context User user) {
        try {
            ArtistFeedback feedbackReturn = Model.getInstance()
                    .getSpecialFeedbacks()
                    .putArtistFeedback(feedback, user.getId());
            LoggerProvider.getLogger().writeEntry(new ArtistFeedbackEntry(user, feedbackReturn));
            return Response.status(200).entity(feedbackReturn).build();
        } catch (Exception e) {
            System.err.println("Error adding artist feedback for artist " + feedback.getArtist() + ":");
            e.printStackTrace();
            return Response.status(400).build();
        }
    }


}