package de.reactivejukebox.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/hello")
public class ExampleClass {
    @Context
    SecurityContext securityContext;

    @Secured
    @GET
    @Path("/{param}")
    public Response getMessage(@PathParam("param") String message) {
        String username = securityContext.getUserPrincipal().getName();
        String output = "Jersey says " + username;
        return Response.status(200).entity(output).build();
    }

    @POST
    @Secured
    @Path("/{param}")
    public Response getMessage2(@PathParam("param") String message) {
        String username = securityContext.getUserPrincipal().getName();
        String output = "Jersey says " + username;
        return Response.status(200).entity(output).build();
    }
}