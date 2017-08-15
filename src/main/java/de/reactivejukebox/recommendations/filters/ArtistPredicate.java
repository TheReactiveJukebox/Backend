package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.Artist;
import de.reactivejukebox.model.Track;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Returns TRUE if Track belongs to given Artist.
 */
public class ArtistPredicate implements Predicate<Track> {
    private Set<Artist> artists;


    public ArtistPredicate(Set<Artist> artists) {
        this.artists = artists;
    }

    public ArtistPredicate(Artist artist) {
        this.artists = new HashSet<>();
        this.artists.add(artist);
    }

    @Override
    public boolean test(Track track) {
        return artists.contains(track.getArtist());
    }
}
