package de.reactivejukebox.api;

import de.reactivejukebox.user.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.HeaderParam;

@Path("/hello")
public class ExampleClass {
    @GET
    @Path("/{param}")
    public Response getMessage(@HeaderParam("Authorization") String token,@PathParam("param") String message) {
        Token t = new Token(token);
        TokenHandler.getTokenHandler().
        TokenHandler.getTokenHandler().getUser(t);
        String output = "Jersey says " + message;
        return Response.status(200).entity(output).build();
    }
}