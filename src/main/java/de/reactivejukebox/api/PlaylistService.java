package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.PlaylistPlain;
import de.reactivejukebox.model.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/playlist")
public class PlaylistService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/")
    public List<PlaylistPlain> getPlaylists(
            @QueryParam("id") int id,
            @QueryParam("userid") int userid,
            @Context User user) {
        if (id != 0) {
            ArrayList<PlaylistPlain> results = new ArrayList<>();
            results.add(Model.getInstance()
                    .getPlaylists()
                    .getById(id)
            );
            return results;
        } else if (userid != 0) {
            return Model.getInstance()
                    .getPlaylists()
                    .getByUser(userid)
                    .stream()
                    .filter(PlaylistPlain::isPublic)
                    .collect(Collectors.toList());
        } else {
            return Model.getInstance()
                    .getPlaylists()
                    .getByUser(userid);
        }
    }
}
