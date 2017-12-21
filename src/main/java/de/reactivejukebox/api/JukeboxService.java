package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.logger.LoggerProvider;
import de.reactivejukebox.logger.RadioCreateEntry;
import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.RecommendationStrategyFactory;
import de.reactivejukebox.recommendations.strategies.StrategyType;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/jukebox")
public class JukeboxService {

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getCurrentRadiostation(@Context User user) {
        try {
            Radio radio = Model.getInstance().getRadios().getByUserId(user.getId());
            return Response.ok(radio.getPlainObject()).build();
        } catch (SQLException e) {
            System.err.println("Error getting current radiostation for user " + user.getUsername() + ":");
            e.printStackTrace();
            return Response.status(404)
                    .build();
        }
    }

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createJukebox(RadioPlain r, @Context User user) {
        try {
            r.setUserId(user.getId());
            Radio radio = Model.getInstance().getRadios().put(r);
            LoggerProvider.getLogger().writeEntry(new RadioCreateEntry(user, radio));
            return Response.ok(radio.getPlainObject()).build();
        } catch (SQLException e) {
            System.err.println("Error creating radiostation for user " + user.getUsername() + ":");
            e.printStackTrace();
            return Response.status(503)
                    .build();
        }
    }

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/next")
    public Response getNextSongs(@Context User user,
                                 @QueryParam("count") int count,
                                 @QueryParam("upcoming") List<Integer> upcoming) {
        TrackFeedbacks feedback = Model.getInstance().getTrackFeedbacks();
        try {
            // upcomingTracks contains tracks that are already in the listening queue
            List<Track> upcomingTracks = upcoming.stream()
                    .map(i -> Model.getInstance().getTracks().get(i))
                    .collect(Collectors.toList());
            // build algorithm for user's current jukebox
            Radio radio = Model.getInstance().getRadios().getByUserId(user.getId());

            RecommendationStrategy algorithm = new RecommendationStrategyFactory(radio, upcomingTracks)
                    .createStrategy(count);
            // obtain results
            List<TrackPlain> results = algorithm.getRecommendations().getTracks().stream()
                    .map(Track::getPlainObject)
                    .collect(Collectors.toList());
            for (TrackPlain r : results) {
                r.setFeedback(feedback.get(r.getId(), user.getId()));
            }

            return Response.ok(results).build();
        } catch (SQLException e) {
            System.err.println("Error getting next songs for current radiostation of user " + user.getUsername() + ":");
            e.printStackTrace();
            return Response.status(502)
                    .build();
        }
    }

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/algorithms")
    public Response getAlgorithms() {
        List<String> algorithms = Arrays.stream(StrategyType.values()).map(Enum::name).collect(Collectors.toList());
        return Response.ok(algorithms).build();
    }
}
