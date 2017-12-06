package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.datahandlers.TokenHandler;
import de.reactivejukebox.logger.Entry;
import de.reactivejukebox.logger.Event;
import de.reactivejukebox.logger.LoggerProvider;
import de.reactivejukebox.model.UserPlain;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserService {

    /**
     * Consumes JSON File with username and password as @param auth
     * Delivers Token on successful login
     * Status 409 for invalid password or username
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response login(UserPlain auth) {
        System.out.printf("login" + auth);
        try {
            UserPlain token = new TokenHandler().checkUser(auth);
            LoggerProvider.getLogger().writeEntry(new Entry(Event.USER_LOGIN, token));
            return Response.ok(token).build();
        } catch (Exception e) {
            return Response.status(442).entity("invalid password or username").build();
        }
    }

    /**
     * Consumes JSON File with Token as @param auth
     * Delivers Token on successful login
     * Status 409 for invalid token
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/autologin")
    public Response autoLogin(UserPlain auth) {
        System.out.printf("autologin " + auth);
        try {
            UserPlain token = new TokenHandler().checkToken(auth);
            return Response.ok(token).build();
        } catch (Exception e) {
            return Response.status(409).entity("no valid token").build();
        }
    }

    /**
     * Consumes JSON File with token as @param auth
     * invalidates token
     * returns Status 200 logged out
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/logout")
    public Response logout(UserPlain auth) {
        try {
            new TokenHandler().logout(auth);
            return Response.status(200).entity("logged out").build();
        } catch (Exception e) {
            return Response.status(409).entity("no valid token").build();
        }
    }

    /**
     * Consumes JSON File with username and password as @param auth
     * Delivers Token on successful register
     * Status 409 if username is already taken or Invite Key is wrong
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response register(UserPlain auth) {
        System.out.printf("register " + auth);
        try {
            try {
                if (auth.getInviteKey().matches("xxx")) {
                    auth.setInviteKey(null);
                } else {
                    return Response.status(441).entity("invalid InviteKey").build();
                }
            } catch (Exception e) {
                return Response.status(441).entity("invalid InviteKey").build();
            }
            UserPlain token = new TokenHandler().register(auth);
            return Response.ok(token).build();
        } catch (Exception e) {
            return Response.status(440).entity("username already in use").build();
        }
    }

    /**
     * Checks Token wir @Secured and returns Status 200 for registered Token
     */
    @GET
    @Secured
    @Path("/basicAuth")
    public Response basicAuth() {
        return Response.status(200).build();
    }

}