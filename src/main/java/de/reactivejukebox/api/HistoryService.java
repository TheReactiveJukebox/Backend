package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.datahandlers.HistoryHandler;
import de.reactivejukebox.logger.HistoryDeleteEntry;
import de.reactivejukebox.logger.HistoryPostEntry;
import de.reactivejukebox.logger.LoggerProvider;
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
            LoggerProvider.getLogger().writeEntry(new HistoryPostEntry(user, historyEntry));
            return Response.ok().entity(historyEntry).build();
        } catch (SQLException e) {
            System.err.println("Error while adding history entry:");
            e.printStackTrace();
            return Response.status(501).build();
        }
    }

    @DELETE
    @Secured
    @Path("/")
    public Response deleteEntry(@Context User user, @QueryParam("id") Integer historyId) {
        try {
            new HistoryHandler().deleteHistoryEntry(historyId, user);
            LoggerProvider.getLogger().writeEntry(new HistoryDeleteEntry(user, historyId));
            return Response.ok().build();
        } catch (SQLException e) {
            System.err.println("Error while deleting history entry:");
            e.printStackTrace();
            return Response.status(501).build();
        }
    }
}
