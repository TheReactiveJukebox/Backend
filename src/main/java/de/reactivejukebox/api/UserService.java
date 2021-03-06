package de.reactivejukebox.api;

import de.reactivejukebox.JukeboxConfig;
import de.reactivejukebox.core.Secured;
import de.reactivejukebox.datahandlers.TokenHandler;
import de.reactivejukebox.logger.*;
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
        try {
            System.out.printf("login" + auth);
            UserPlain token = new TokenHandler().checkUser(auth);
            LoggerProvider.getLogger().writeEntry(new UserLoggedInEntry(token));
            return Response.ok(token).build();
        } catch (Exception e) {
            System.err.println("Invalid username or password:");
            return Response.status(442).entity("Invalid username or password").build();
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
        try {
            System.out.printf("autologin " + auth);
            UserPlain token = new TokenHandler().checkToken(auth);
            LoggerProvider.getLogger().writeEntry(new UserAutoLoggedInEntry(token));
            return Response.ok(token).build();
        } catch (Exception e) {
            System.err.println("Invalid authorization token");
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
            LoggerProvider.getLogger().writeEntry(new UserLoggedOutEntry(auth));
            return Response.status(200).entity("logged out").build();
        } catch (Exception e) {
            System.err.println("Invalid authorization token");
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
        try {
            System.out.printf("register " + auth);
            try {
                if (auth.getInviteKey().equals(JukeboxConfig.JUKEBOX_INVITE_KEY)) {
                    auth.setInviteKey(null);
                } else {
                    return Response.status(441).entity("invalid invite key").build();
                }
            } catch (Exception e) {
                System.err.println("Invalid invite key");
                return Response.status(441).entity("invalid invite key}").build();
            }
            UserPlain token = new TokenHandler().register(auth);
            LoggerProvider.getLogger().writeEntry(new UserRegisterEntry(token));
            return Response.ok(token).build();
        } catch (Exception e) {
            System.err.println("Username already in use");
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