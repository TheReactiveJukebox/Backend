package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.logger.ArtistFeedbackEntry;
import de.reactivejukebox.logger.LoggerProvider;
import de.reactivejukebox.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Path("/")
public class FeedbackService {

    @GET
    @Path("/tempo/feedback")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeedback(@Context User user) {
        try {
            List<SpeedFeedback> result = Model.getInstance().getSpecialFeedbacks().getAllSpeedFeedback(user.getId());
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            System.err.println("Error getting Tempo feedback:");
            e.printStackTrace();
            return Response.status(400).build();
        }
    }

    @POST
    @Path("/tempo/feedback")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFeedback(SpeedFeedback feedback, @Context User user) {
        try {
            SpeedFeedback feedbackReturn = Model.getInstance()
                    .getSpecialFeedbacks()
                    .putSpeedFeedback(feedback, user.getId());
            //TODO LoggerProvider.getLogger().writeEntry(new ArtistFeedbackEntry(user, feedbackReturn));
            return Response.status(200).entity(feedbackReturn).build();
        } catch (Exception e) {
            System.err.println("Error adding artist feedback for artist " + feedback.getFSpeed() + ":");
            e.printStackTrace();
            return Response.status(400).build();
        }
    }

    @GET
    @Path("/mood/feedback")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMoodFeedback(@Context User user) {
        try {
            List<MoodFeedback> result = Model.getInstance().getSpecialFeedbacks().getAllMoodFeedback(user.getId());
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            System.err.println("Error getting Tempo feedback:");
            e.printStackTrace();
            return Response.status(400).build();
        }
    }

    @POST
    @Path("/mood/feedback")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMoodFeedback(MoodFeedback feedback, @Context User user) {
        try {
             MoodFeedback feedbackReturn = Model.getInstance()
                     .getSpecialFeedbacks()
                     .putMoodFeedback(feedback, user.getId());
            //TODO LoggerProvider.getLogger().writeEntry(new ArtistFeedbackEntry(user, feedbackReturn));
            return Response.status(200).entity(feedbackReturn).build();
        } catch (Exception e) {
            System.err.println("Error adding artist feedback for artist " + feedback.getFMood() + ":");
            e.printStackTrace();
            return Response.status(400).build();
        }
    }
}
