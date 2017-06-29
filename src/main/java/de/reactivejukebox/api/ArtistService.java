package de.reactivejukebox.api;

import de.reactivejukebox.model.Artist;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/artist")
public class ArtistService {

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@QueryParam("name") String name) {
        // TODO replace mock data with actual data
        Artist mockArtist = new Artist();
        mockArtist.setName("Red Hot Chili Peppers");

        return Response.status(200)
                .entity(new Artist[]{mockArtist})
                .build();
    }
}
