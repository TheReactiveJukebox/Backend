package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/genre")
public class GenreService {
    @GET
    @Path("")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenreList() {
        List<String> result = Model.getInstance().getGenres().metaList();
        return Response.status(200)
                .entity(result)
                .build();
    }

    @GET
    @Path("/feedback")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeedback(List<String> genres, @Context User user){
        //TODO
        List<GenreFeedback> gf = new ArrayList<>();
        gf.add(new GenreFeedback());
        return Response.status(200).entity(gf).build();
    }

    @POST
    @Path("/feedback")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFeedback(List<GenreFeedback> feedback, @Context User user){
        //TODO
        List<GenreFeedback> gf = new ArrayList<>();
        gf.add(new GenreFeedback());
        return Response.status(200).entity(gf).build();
    }

}
