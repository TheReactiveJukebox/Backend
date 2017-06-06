package de.reactivejukebox.api;

import de.reactivejukebox.user.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/hello")
public class ExampleClass {
    @GET
    @Path("/{param}")
    public Response getMessage(@PathParam("param") String message) {
        TokenHandler.getTokenHandler().getUser(new Token());
        String output = "Jersey says " + message;
        return Response.status(200).entity(output).build();
    }
}