package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.logger.GenreFeedbackEntry;
import de.reactivejukebox.logger.LoggerProvider;
import de.reactivejukebox.model.GenreFeedback;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/genre")
public class GenreService {
    @GET
    @Path("")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenreList() {
        try {
            List<String> result = Model.getInstance().getGenres().metaList();
            return Response.status(200)
                    .entity(result)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Internal Error").build();
        }
    }

    @GET
    @Path("/feedback")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeedback(@QueryParam("id") List<String> id, @Context User user) {
        try {
            return Response.status(200).entity(Model.getInstance().getSpecialFeedbacks().getGenreFeedback(id, user.getId())).build();
        } catch (Exception e) {
            System.err.println("Error getting genre feedback:");
            e.printStackTrace();
            return Response.status(400).build();
        }

    }

    @POST
    @Path("/feedback")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFeedback(GenreFeedback feedback, @Context User user) {
        try {
            GenreFeedback feedbackReturn = Model.getInstance()
                    .getSpecialFeedbacks()
                    .putGenreFeedback(feedback, user.getId());
            LoggerProvider.getLogger().writeEntry(new GenreFeedbackEntry(user, feedbackReturn));
            return Response.status(200).entity(feedbackReturn).build();
        } catch (Exception e) {
            System.err.println("Error adding genre feedback for genre "+feedback.getGenre()+":");
            e.printStackTrace();
            return Response.status(400).build();
        }
    }
}
