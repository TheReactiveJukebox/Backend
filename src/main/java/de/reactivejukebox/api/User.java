package de.reactivejukebox.api;

import de.reactivejukebox.user.Token;
import de.reactivejukebox.user.TokenHandler;
import de.reactivejukebox.user.UserData;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class User {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response login(UserData auth) {
        System.out.printf("login" + auth);
        try {
            Token token = TokenHandler.getTokenHandler().checkUser(auth);
            return Response.ok(token).build();
        } catch (Exception e) {
            return Response.status(409).entity("invalid password or username").build();
        }
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/autologin")
    public Response login(Token auth) {
        System.out.printf("autologin " + auth);
        try {
            // TODO Return token
            TokenHandler.getTokenHandler().checkToken(auth);
            return Response.ok(auth).build();
        } catch (Exception e) {
            return Response.status(409).entity("invalid password or username").build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/logout")
    public Response logout(Token auth) {
        System.out.printf("logout " + auth);
        //TODO logout Method
        //TokenHandler.getTokenHandler().
        return Response.status(200).entity("logged out").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response register(UserData auth) {
        System.out.printf("register " + auth);
        try {
            Token token = TokenHandler.getTokenHandler().register(auth);
            return Response.ok(token).build();
        } catch (Exception e) {
            return Response.status(409).entity("invalid password or username").build();
        }
    }

}