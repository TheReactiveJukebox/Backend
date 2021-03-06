package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.database.Database;
import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.datahandlers.TrackFeedbackHandler;
import de.reactivejukebox.logger.ActionFeedbackEntry;
import de.reactivejukebox.logger.LoggerProvider;
import de.reactivejukebox.logger.SongFeedbackEntry;
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


/**
 * Class for handling things concerning tracks, such as providing information on tracks or giving feedback to certain tracks
 */
@Path("/track")
public class TrackService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/")
    public Response search(@QueryParam("id") List<Integer> id,
                           @QueryParam("titlesubstr") String titleSubstring,
                           @QueryParam("artist") int artist,
                           @QueryParam("album") int album,
                           @QueryParam("count") int countResults,
                           @Context User user) {
        List<TrackPlain> result = null;
        try {
        Set<Integer> ids = new TreeSet<>(id);
        Stream<Track> s = Model.getInstance().getTracks().stream();
        if (!ids.isEmpty()) {
            s = s.filter(track -> ids.contains(track.getId()));
        }
        if (titleSubstring != null) {
            Database db = DatabaseProvider.getInstance().getDatabase();
            s = s.filter(track ->
                    db.normalize(track.getTitle()).contains(db.normalize(titleSubstring)));
        }
        if (artist != 0) {
            s = s.filter(track -> track.getArtist().getId() == artist);
        }
        if (album != 0) {
            s = s.filter(track -> track.getAlbum().getId() == album);
        }
        result = s.map(Track::getPlainObject).collect(Collectors.toList());
        TrackFeedbacks feedback = Model.getInstance().getTrackFeedbacks();
            for (TrackPlain r : result) {
                r.setFeedback(feedback.get(r.getId(), user.getId()));
            }
		return Response.status(200)
                .entity(result)
                .build();
        } catch (SQLException e){
            System.err.println("Error setting track feedback into track");
            e.printStackTrace();
            return Response.status(200)
                .entity(result)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(501).entity("Internal Error").build();
        }

    }

    /**
     * Post feedback to a track with a given id
     *
     * @param feedback posted feedback
     * @param user     user who gave the feedback
     * @return TrackFeedback Object of the feedback actually written to the DB
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/feedback")
    public Response pushTrackFeedback(TrackFeedback feedback, @Context User user) {

        try {
            TrackFeedback feedbackReturn = new TrackFeedbackHandler().addTrackFeedback(feedback, user);
            LoggerProvider.getLogger().writeEntry(new SongFeedbackEntry(user, feedbackReturn));
            return Response.ok().entity(feedbackReturn).build();
        } catch (SQLException e) {
            System.err.println("Error pushing track feedback concerning track " + feedback.getTrackId() + ":");
            e.printStackTrace();
            return Response.status(501).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(501).entity("Internal Error").build();
        }
    }

    /**
     * Post indirect feedback to the database
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/indirect-feedback")
    public Response pushIndirectFeedback(IndirectFeedbackPlain feedbackPlain, @Context User user) {
        try {
            feedbackPlain.setUserId(user.getId());
            // Validate input
            if (!feedbackPlain.isValid()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            // Process input
            IndirectFeedbackPlain feedbackReturn = Model.getInstance().getIndirectFeedbackEntries().put(feedbackPlain);
            // Build response
            LoggerProvider.getLogger().writeEntry(new ActionFeedbackEntry(user, feedbackReturn));
            return Response.ok().entity(feedbackReturn).build();
        } catch (Exception e) {
            System.err.println("Error pushing indirect feedback concerning track " + feedbackPlain.getTrackId() + ":");
            e.printStackTrace();
            return Response.status(501).entity("Internal Error").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/parameter")
    public Response earliest() {
        try {
            return Response.ok().entity(Model.getInstance().getTracks().getTrackParameter()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(501).entity("Internal Error").build();
        }

    }

}
