package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.datahandlers.TendencyHandler;
import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.RecommendationStrategyFactory;
import de.reactivejukebox.recommendations.strategies.StrategyType;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLDataException;
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
            e.printStackTrace();
            return Response.status(503)
                    .entity("Error no Radiostation available")
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
            return Response.ok(radio.getPlainObject()).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(503)
                    .entity("Error while writing/reading database")
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
            List<MusicEntityPlain> results = algorithm.getRecommendations().stream()
                    .map(Track::getPlainObject)
                    .collect(Collectors.toList());
            return Response.ok(results).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(502)
                    .entity("Error while communicating with database.")
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

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tendency")
    public Response pushTendency(TendencyPlain tendency, @Context User user) {

        tendency.setUserId(user.getId());

        try {
            TendencyPlain tendencyReturn = new TendencyHandler().addTendency(tendency, user).getPlainObject();
            return Response.ok().entity(tendencyReturn).build();
        } catch (SQLDataException e) {
            return Response.status(400).entity(e).build();

        } catch (SQLException e) {
            return Response.status(500).entity(e).build();
        }

    }
}
