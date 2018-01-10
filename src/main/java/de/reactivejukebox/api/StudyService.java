package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.logger.LoggerProvider;
import de.reactivejukebox.logger.QuestionnaireEntry;
import de.reactivejukebox.model.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/study")
public class StudyService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    @Secured
    public Response createQuestionnaire(@Context User user) {
        try {
            LoggerProvider.getLogger().writeEntry(new QuestionnaireEntry(user));
            return Response.ok("{}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Internal Error").build();
        }
    }
}
