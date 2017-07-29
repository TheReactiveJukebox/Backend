package de.reactivejukebox.recommendations.traits;

import de.reactivejukebox.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ArtistPredicate implements Predicate<Track> {
    private Set<Artist> artists;


    public ArtistPredicate(Set<Artist> artists){
        this.artists = artists;
    }

    @Override
    public boolean test(Track track) {
        return artists.contains(track.getArtist());
    }
}
