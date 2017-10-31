package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.PlaylistPlain;
import de.reactivejukebox.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    @Secured
    public Response createPlaylist(PlaylistPlain playlist, @Context User user) {
        playlist.setUserId(user.getId());
        try {
            playlist = Model.getInstance().getPlaylists().add(playlist);
            return Response.ok(playlist).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}
