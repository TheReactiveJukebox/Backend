package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.FrontendLog;
import de.reactivejukebox.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/logging")
public class LoggingService {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    @Secured
    public Response createLog(@Context User user, FrontendLog log) {
        // TODO log
        return Response.ok("{}").build();
    }
}
