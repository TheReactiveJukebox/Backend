package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.database.Database;
import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.MusicEntityPlain;
import de.reactivejukebox.model.Track;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/track")
public class TrackService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/")
    public Response search(@QueryParam("id") List<Integer> id,
                           @QueryParam("titlesubstr") String titleSubstring,
                           @QueryParam("artist") int artist,
                           @QueryParam("count") int countResults) {
        List<MusicEntityPlain> result;
        Set<Integer> ids = new TreeSet<>(id);
        Stream<Track> s = Model.getInstance().getTracks().stream();
        if (!ids.isEmpty()) {
            s = s.filter(track -> ids.contains(track.getId()));
        }
        if (titleSubstring != null) {
            Database db = DatabaseProvider.getInstance().getDatabase();
            s = s.filter(track ->
                    db.normalize(track.getTitle()).startsWith(db.normalize(titleSubstring)));
        }
        if (artist != 0) {
            s = s.filter(track -> track.getArtist().getId() == artist);
        }
        result = s.map(Track::getPlainObject).collect(Collectors.toList());
        return Response.status(200)
                .entity(result)
                .build();

    }
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/feedback")
	public Response pushTrackFeedback (@QueryParam("id") Integer id) {
		//TODO: implement
		return Response.status(500);
	}
	
	
	
}