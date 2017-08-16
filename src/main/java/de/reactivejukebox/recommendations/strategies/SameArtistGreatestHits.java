package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.filters.ArtistPredicate;
import de.reactivejukebox.recommendations.filters.HistoryFilter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SameArtistGreatestHits implements RecommendationStrategy {

    private Collection<Track> base;
    private int resultCount;
    private Tracks tracks;
    private Radio radio;
    private HistoryFilter historyFilter;
    private Collection<Track> upcoming;

    public SameArtistGreatestHits(Radio radio, Collection<Track> upcoming, int resultCount) {
        this(radio, upcoming, resultCount, Model.getInstance().getTracks());
    }

    public SameArtistGreatestHits(Radio radio, Collection<Track> upcoming, int resultCount, Tracks tracks) {
        this.resultCount = resultCount;
        this.radio = radio;
        this.upcoming = upcoming;
        this.base = radio.getStartTracks();
        this.tracks = tracks;
        this.historyFilter = new HistoryFilter(radio, upcoming, resultCount);
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
        // historyFilter by History and Genre
        Stream<Track> possibleTracks = tracks.stream().filter(new ArtistPredicate(a));                // historyFilter by Artists
        possibleTracks = a.filter(possibleTracks);
        return historyFilter.forHistory(possibleTracks).sorted(Comparator.comparingInt(Track::getPlayCount).reversed()); // sort
    }
}
