package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Artist;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SameArtistGreatestHits implements RecommendationStrategy {

    private static final int HITS_PER_ARTIST = 5;
    private Collection<Track> base;

    public SameArtistGreatestHits(Collection<Track> base) {
        this.base = base;
    }

    @Override
    public List<Track> getRecommendations() {
        return base.stream()
                .map(Track::getArtist) // get artist for each track
                .distinct() // eliminate duplicates
                .flatMap(this::greatestHits) // get every artist's greatest hits in a single stream
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed()) // sort by play count, descending
                .collect(Collectors.toList()); // collect into list
    }

    private Stream<Track> greatestHits(Artist a) {
        return Model.getInstance().getTracks().stream()
                .filter(track -> track.getArtist() == a) // get all tracks for artist
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed()) // sort by popularity
                .limit(HITS_PER_ARTIST); // get first HITS_PER_ARTIST tracks
    }
}
