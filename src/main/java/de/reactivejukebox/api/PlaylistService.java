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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/playlist")
public class PlaylistService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/")
    public Response getPlaylists(
            @QueryParam("id") int id,
            @QueryParam("userid") int userid,
            @Context User user) {
        List<PlaylistPlain> results;
        if (id != 0) {
            results = new LinkedList<>();
            results.add(Model.getInstance()
                    .getPlaylists()
                    .getById(id)
            );
        } else if (userid != 0) {
            // return all public playlists of other user
            results = Model.getInstance()
                    .getPlaylists()
                    .getByUser(userid)
                    .stream()
                    .filter(PlaylistPlain::isPublic)
                    .collect(Collectors.toList());
        } else {
            // return all playlists of current user
            results = Model.getInstance()
                    .getPlaylists()
                    .getByUser(user.getId());
        }
        return Response.ok(results).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    @Secured
    public Response createPlaylist(PlaylistPlain playlist, @Context User user) {
        if (playlist == null || playlist.getTitle() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Playlist is not valid.")
                    .build();
        }
        playlist.setUserId(user.getId());
        playlist.setCreated(new Date());
        playlist.setEdited(new Date());
        try {
            playlist = Model.getInstance().getPlaylists().add(playlist);
            return Response.ok(playlist).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    @Secured
    public Response updatePlaylist(PlaylistPlain playlist, @Context User user) {
        if (playlist == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No playlist given.").build();
        }
        PlaylistPlain oldPlaylist = Model.getInstance().getPlaylists().getById(playlist.getId());
        // check if the user is authorized to change the playlist
        if (oldPlaylist == null
                || oldPlaylist.getUserId() != user.getId()
                || playlist.getUserId() != oldPlaylist.getUserId()) {
            return Response.status(403).entity("User not authorized to change this playlist.").build();
        }
        // execute Update
        playlist.setEdited(new Date());
        if (!Model.getInstance().getPlaylists().update(playlist)) {
            return Response.serverError().entity("Could not update playlist due to internal error.").build();
        }
        return Response.ok(playlist).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    @Secured
    public Response removePlaylist(@QueryParam("id") int id, @Context User user) {
        PlaylistPlain playlist = Model.getInstance().getPlaylists().getById(id);
        if (playlist == null) {
            return Response.status(404).entity("Playlist not found.").build();
        }
        if (playlist.getUserId() != user.getId()) {
            return Response.status(403).entity("User not authorized to remove this playlist.").build();
        }
        if (!Model.getInstance().getPlaylists().remove(id)) {
            return Response.serverError().entity("Could not remove playlist due to internal error.").build();
        }
        return Response.ok().build();
    }
}
