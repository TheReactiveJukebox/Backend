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

    private Collection<Track> history;
    private Collection<Track> base;
    private int resultCount;
    private Model model;

    public SameArtistGreatestHits(Collection<Track> history, Collection<Track> base, int resultCount) {
        this(history, base, resultCount, Model.getInstance());
    }

    public SameArtistGreatestHits(Collection<Track> history, Collection<Track> base, int resultCount, Model model) {
        this.resultCount = resultCount;
        this.history = history;
        this.base = base;
        this.model = model;
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
        return model.getTracks().stream()
                .filter(track -> !history.contains(track)) // ignore recent history
                .filter(track -> track.getArtist() == a) // get all tracks for artist
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed()); // sort by popularity
    }
}
