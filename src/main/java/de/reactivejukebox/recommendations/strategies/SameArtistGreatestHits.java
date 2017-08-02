package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.traits.ArtistPredicate;
import de.reactivejukebox.recommendations.traits.Filter;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SameArtistGreatestHits implements RecommendationStrategy {

    private Collection<Track> base;
    private int resultCount;
    private Tracks tracks;
    private Radio radio;
    private Filter filter;
    private Collection<Track> upcoming;

    public SameArtistGreatestHits(Radio radio, Collection<Track> upcoming, int resultCount) {
        this(radio,upcoming, resultCount, Model.getInstance().getTracks());
    }

    public SameArtistGreatestHits(Radio radio, Collection<Track> upcoming, int resultCount, Tracks tracks) {
        this.resultCount = resultCount;
        this.radio = radio;
        this.upcoming = upcoming;
        this.base = radio.getStartTracks();
        this.tracks = tracks;
        this.filter = new Filter(radio, upcoming, resultCount);
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
         // filter by History and Genre
        Stream<Track> possibleTracks =  tracks.stream().filter(new ArtistPredicate(a));                // filter by Artists
        return filter.byRadio(possibleTracks).sorted(Comparator.comparingInt(Track::getPlayCount).reversed()); // sort
    }
}
