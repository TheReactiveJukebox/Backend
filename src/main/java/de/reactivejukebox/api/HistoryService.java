package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.HistoryEntry;
import de.reactivejukebox.model.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
    @Path("/")
    public Response getMessage(@Context User user, HistoryEntry history) {
        try {
            de.reactivejukebox.feedback.History.addHistoryEntry(history, user);
            return Response.status(200).build();
        }catch (SQLException e){
            return Response.status(500).entity(e).build();
        }
    }
}
