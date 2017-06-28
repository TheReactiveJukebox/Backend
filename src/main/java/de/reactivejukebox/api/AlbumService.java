package de.reactivejukebox.api;

import de.reactivejukebox.model.Album;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/album")
public class AlbumService {

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@QueryParam("title") String title) {
        // TODO replace mock data with actual data
        Album mockAlbum = new Album();
        mockAlbum.setArtist("Red Hot Chili Peppers");
        mockAlbum.setTitle("Stadium Arcadium");

        return Response.status(200)
                .entity(new Album[]{mockAlbum})
                .build();
    }
}
