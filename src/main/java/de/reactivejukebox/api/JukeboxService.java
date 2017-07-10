package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.datahandlers.RadioHandler;
import de.reactivejukebox.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/jukebox")
public class JukeboxService {

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getCurrentRadiostation(@Context User user) {

        try {
            RadioPlain radio = new RadioHandler().getRadiostation(user);
            return Response.ok(radio)
                    .build();
        }catch(SQLException e){
            return Response.status(503)
                    .entity("Error no Radiostation available")
                    .build();
        }
    }

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createJukebox(RadioPlain r, @Context User user) {
        try {
            RadioPlain radio = new RadioHandler().addRadiostation(r,user);
            return Response.ok(radio)
                    .build();
            } catch (SQLException e) {
            return Response.status(503)
                    .entity("Error while writing/reading database")
                    .build();
        }
    }

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/next")
    public Response getNextSongs(@Context User user, @QueryParam("count") int count) {
        try {
            List<TrackPlain> results = new RadioHandler().getSongs(count, user);
            return Response.ok(results)
                    .build();
        } catch (SQLException e) {
            return Response.status(502)
                    .entity("Error while communicating with database.")
                    .build();
        }
    }
}
