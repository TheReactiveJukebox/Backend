package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Artist;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.Tracks;
import de.reactivejukebox.recommendations.RecommendationStrategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SameArtistGreatestHits implements RecommendationStrategy {

    private Predicate<Track> history;
    private Collection<Track> base;
    private int resultCount;
    private Tracks tracks;

    public SameArtistGreatestHits(Predicate<Track> history, Collection<Track> base, int resultCount) {
        this(history, base, resultCount, Model.getInstance().getTracks());
    }

    public SameArtistGreatestHits(Predicate<Track> history, Collection<Track> base, int resultCount, Tracks tracks) {
        this.resultCount = resultCount;
        this.history = history;
        this.base = base;
        this.tracks = tracks;
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
        return tracks.stream()
                .filter(history) // ignore recent history
                .filter(track -> track.getArtist() == a) // get all tracks for artist
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed()); // sort by popularity
    }
}
