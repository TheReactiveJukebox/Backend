package de.reactivejukebox.api;

import de.reactivejukebox.user.UserData;

import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


@Path("/ex")
public class ExampleClass {

    @Secured
    @GET
    @Path("/test/{param}")
    public Response getMessage(@Context UserData user, @PathParam("param") String message) {
        String output = "Jersey says " + user;
        return Response.status(200).entity(output).build();
    }

    @GET
    @Secured
    @Path("/hello/{param}")
    public Response getMessage2(@Context ContainerRequestContext crc, @PathParam("param") String message) {
       UserData user = (UserData)crc.getProperty("UserData");
        String output = "Jersey says " + user;//username;
        return Response.status(200).entity(output).build();
    }
}