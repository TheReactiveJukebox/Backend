package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.Recommendations;
import de.reactivejukebox.recommendations.filters.ArtistPredicate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SameArtistGreatestHits implements RecommendationStrategy {

    private Collection<Track> base;
    private int resultCount;
    private Tracks tracks;
    private Radio radio;
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
    }

    @Override
    public Recommendations getRecommendations() {
        List<Track> recs = base.stream()
                .map(Track::getArtist) // get artist for each track
                .distinct() // eliminate duplicates
                .flatMap(this::greatestHits) // get every artist's greatest hits in a single stream
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed()) // sort by play count, descending
                .limit(resultCount) // get first resultCount Tracks
                .collect(Collectors.toList()); // collect into list

        // find maximum play count
        OptionalInt max = recs.stream().mapToInt(Track::getPlayCount).max();

        // if no max is found, the stream was empty
        if (!max.isPresent()) {
            return new Recommendations(recs, new ArrayList<>());
        }

        // otherwise score tracks relative to max
        ArrayList<Float> scores = new ArrayList<>();
        for (Track t : recs) {
            scores.add((float) t.getPlayCount() / (float) max.getAsInt());
        }
        return new Recommendations(recs, scores);
    }

    private Stream<Track> greatestHits(Artist a) {
        return tracks.stream()
                .filter(new ArtistPredicate(a)) // filter by artist
                .filter(track -> !upcoming.contains(track)) // filter upcoming
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed()); // sort
    }
}
