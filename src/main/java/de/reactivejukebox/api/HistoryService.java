package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.datahandlers.HistoryHandler;
import de.reactivejukebox.model.HistoryEntryPlain;
import de.reactivejukebox.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/history")
public class HistoryService {

    /**
     * Consumes JSON File with HistoryEntry as @param history
     * Returns Status 200 if successful
     * Status 500 if
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createHistoryEntry(@Context User user, HistoryEntryPlain history) {
        try {
            HistoryEntryPlain historyEntry = new HistoryHandler().addHistoryEntry(history, user);
            return Response.ok().entity(historyEntry).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500).entity("Error while commmunicating with database").build();
        }
    }

    @DELETE
    @Secured
    @Path("/")
    public Response deleteEntry(@QueryParam("id") Integer historyId, @Context User user) {
        try {
            new HistoryHandler().deleteHistoryEntry(historyId, user);
            return Response.ok().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500).entity("Error while commmunicating with database").build();
        }
    }
}
