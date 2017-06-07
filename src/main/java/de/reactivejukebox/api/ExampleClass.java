package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.user.UserData;

import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/hello")
public class ExampleClass {

    @Secured
    @GET
    @Path("/test/{param}")
    public Response getMessage(@Context UserData user, @PathParam("param") String message) {
        String output = "{\"message\": \"Jersey says " + message + "\"}";
        return Response.status(200).entity(output).build();
    }

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{param}")
    public Response getMessage2(@Context ContainerRequestContext crc, @PathParam("param") String message) {
       UserData user = (UserData)crc.getProperty("UserData");
        String output = "{\"message\": \"Jersey says " + message + "\"}";
        return Response.ok(output).build();
    }
}