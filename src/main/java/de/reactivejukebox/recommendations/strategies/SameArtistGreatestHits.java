package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.strategies.traits.HistoryAwareness;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SameArtistGreatestHits implements RecommendationStrategy  {

    private static final int HITS_PER_ARTIST = 5;
    private Collection<Track> history;
    private Collection<Track> base;
    private int resultCount;

    public SameArtistGreatestHits(Collection<Track> history, Collection<Track> base, int resultCount) {
        this.resultCount = resultCount;
        this.history = history;
        this.base = base;
    }

    @Override
    public List<Track> getRecommendations() {
        return base.stream()
                .map(Track::getArtist) // get artist for each track
                .distinct() // eliminate duplicates
                .flatMap(this::greatestHits) // get every artist's greatest hits in a single stream
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed()) // sort by play count, descending
                .limit(resultCount) // get first resultCount Tracks
                .collect(Collectors.toList()); // collect into list
    }

    private Stream<Track> greatestHits(Artist a) {
        return Model.getInstance().getTracks().stream()
                .filter(track -> !history.contains(track)) // ignore recent history
                .filter(track -> track.getArtist() == a) // get all tracks for artist
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed()) // sort by popularity
                .limit(HITS_PER_ARTIST); // get first HITS_PER_ARTIST tracks
    }
}
