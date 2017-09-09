package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.database.Database;
import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.datahandlers.TrackFeedbackHandler;
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
                           @QueryParam("count") int countResults) {
        List<MusicEntityPlain> result;
        Set<Integer> ids = new TreeSet<>(id);
        Stream<Track> s = Model.getInstance().getTracks().stream();
        if (!ids.isEmpty()) {
            s = s.filter(track -> ids.contains(track.getId()));
        }
        if (titleSubstring != null) {
            Database db = DatabaseProvider.getInstance().getDatabase();
            s = s.filter(track ->
                    db.normalize(track.getTitle()).startsWith(db.normalize(titleSubstring)));
        }
        if (artist != 0) {
            s = s.filter(track -> track.getArtist().getId() == artist);
        }
        if (album != 0) {
            s = s.filter(track -> track.getAlbum().getId() == album);
        }
        result = s.map(Track::getPlainObject).collect(Collectors.toList());
        return Response.status(200)
                .entity(result)
                .build();

    }

    /**
     * Post feedback to a track with a given id
     *
     * @param feedback posted feedback
     * @param user     user who gave the feedback
     * @return TrackFeedbackPlain Object of the feedback actually written to the DB
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/feedback")
    public Response pushTrackFeedback(TrackFeedbackPlain feedback, @Context User user) {

        feedback.setUserId(user.getId());

        try {
            TrackFeedbackPlain feedbackReturn = new TrackFeedbackHandler().addTrackFeedback(feedback, user).getPlainObject();
            return Response.ok().entity(feedbackReturn).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500).build();
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
            IndirectFeedbackEntries.put(feedbackPlain);
            // Build response
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}