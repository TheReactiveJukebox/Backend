package de.reactivejukebox.api;

import de.reactivejukebox.user.*;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;

@Path("/user")
public class User {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response login(UserData auth) {
        Token token = new Token();
        System.out.printf("name " + auth);
        String b = auth.getUsername() + " " + auth.getPassword() + " " + "token";
        token.setToken(b);
        if(auth.getUsername() == auth.getPassword()) {
            return Response.ok(token).build();
        }else{
            return Response.status(409).entity("invalid password or username").build();
        }
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/autologin")
    public Response login(Token auth) {
        Token token = new Token();
        System.out.printf("name " + auth);
        token.setToken(auth.getToken());
        if(token.getToken()!= null) {
            return Response.ok(token).build();
        }else{
            return Response.status(409).entity("invalid password or username").build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/logout")
    public Response logout(Token auth) {
        System.out.printf("name " + auth);
        //TODO
        return Response.status(200).entity("logged out").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response register(UserData auth) {
        System.out.printf("name " + auth);
        Token token = new Token();
        String b = auth.getUsername() + " " + auth.getPassword() + " " + "token";
        token.setToken(b);
        //TODO
        if(auth.getUsername() == auth.getPassword()) {
            return Response.ok(token).build();
        }else{
            return Response.status(409).entity("invalid email or username").build();
        }
    }

}