package de.reactivejukebox.api;

import de.reactivejukebox.core.Secured;
import de.reactivejukebox.model.Track;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/track")
public class TrackService {

    private static final Track[] TRACKS = {
            new Track(2342, "Kryptobar", "3 Foos Down", "The Better Foobar - Deluxe Edition", "https://s3.amazonaws.com/images.sheetmusicdirect.com/AlbumService/ca8a8ef7-f305-374a-833b-b8f621ede068/large.jpg", 223),
            new Track(25, "Kryptonite", "3 Doors Down", "The Better Life - Deluxe Edition", "https://s3.amazonaws.com/images.sheetmusicdirect.com/AlbumService/ca8a8ef7-f305-374a-833b-b8f621ede068/large.jpg", 223),
            new Track(23, "Hells Bells", "AC/DC", "Back in Black", "https://upload.wikimedia.org/wikipedia/en/2/23/HellsBells.jpg", 312),
            new Track(84, "Paint it Black", "The Rolling Stones", "Singles 1965-1967", "http://www.covermesongs.com/wp-content/uploads/2010/09/PaintItBlack-400x400.jpg", 224),
            new Track(424, "Wonderwall", "Oasis", "[What's the Story] Morning Glory?", "https://upload.wikimedia.org/wikipedia/en/1/17/Wonderwall_cover.jpg", 258),
            new Track(2, "Evil Ways", "Santana", "Santana (Legacy Edition)", "https://upload.wikimedia.org/wikipedia/en/8/84/Santana_-_Santana_%281969%29.png", 238),
            new Track(91, "Sweet Child O' Mine", "Guns N' Roses", "Greatest Hits", "https://upload.wikimedia.org/wikipedia/en/1/15/Guns_N%27_Roses_-_Sweet_Child_o%27_Mine.png", 355),
            new Track(153, "Come As You Are", "Nirvana", "Nevermind (Deluxe Edition)", "http://e.snmc.io/lk/f/l/e3aee4167b67150f78c352a0e4d129e5/3736618.jpg", 218),
            new Track(287, "Bodies", "Drowning Pool", "Sinner", "https://upload.wikimedia.org/wikipedia/en/4/49/Drowning_Pool-Bodies_CD_Cover.jpg", 201),
            new Track(42, "Crocodile Rock", "Elton John", "Don't Shooot Me I'm Only The Piano Player", "https://upload.wikimedia.org/wikipedia/en/0/0b/Elton_John_Crocodile_Rock_%282%29.jpg", 235),
            new Track(99, "I Was Made For Loving You", "KISS", "Dynasty (Remastered Version)", "http://streamd.hitparade.ch/cdimages/kiss-i_was_made_for_lovin_you_s.jpg", 217),
            new Track(168, "Come Out and Play", "The Offspring", "Smash", "https://upload.wikimedia.org/wikipedia/en/8/80/TheOffspringSmashalbumcover.jpg", 197),
            new Track(94, "Rock & Roll Queen", "The Subways", "Young For Eternity", "https://images-na.ssl-images-amazon.com/images/I/41T42C5YFEL.jpg", 169)
    };

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/{count}")
    public Track[] getTrackList(@PathParam("count") int count) {
        // TODO replace mock data with actual data
        return Arrays.copyOfRange(TRACKS, 0, count);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    public Response search(@QueryParam("title") String title) {
        // TODO replace mock data with actual data
        return Response.status(200)
                .entity(Arrays.copyOfRange(TRACKS, 0, 4))
                .build();
    }
}