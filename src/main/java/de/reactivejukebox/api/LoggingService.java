package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.FrontendLog;
import de.reactivejukebox.model.User;
import org.apache.logging.log4j.LogManager;

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
        try {
            String msg = user.getUsername() + "-" + user.getId() + " " + log.getMessage();
            LogManager.getLogger("frontend").error(msg);
            return Response.ok("{}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(501).entity("Internal Error").build();
        }
    }
}
