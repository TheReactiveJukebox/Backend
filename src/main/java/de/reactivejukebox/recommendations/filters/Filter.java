package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.*;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Filter {
    private int resultCount;
    private Predicate<Track> history;
    private Predicate<Track> genre;
    private Predicate<Track> published;
    private Radio radio;
    private Collection<Track> upcoming;

    public Filter(Radio radio, Collection<Track> upcoming, int resultCout) {
        this(Model.getInstance().getGenres(), Model.getInstance().getHistoryEntries(), radio, upcoming, resultCout);
    }

    public Filter(Genres genres, HistoryEntries historyEntries, Radio radio, Collection<Track> upcoming, int resultCount) {
        this.radio = radio;
        this.upcoming = upcoming;
        this.history = new HistoryPredicate(historyEntries, radio, upcoming);
        this.genre = new GenrePredicate(genres, radio);
        this.published = new PublishedPredicate(radio);
        this.resultCount = resultCount;
    }


    public Stream<Track> forGenre(Stream<Track> trackStream) {
        trackStream = trackStream.filter(genre);
        return trackStream;
    }

    public Stream<Track> forHistory(Stream<Track> trackStream) {
        trackStream = trackStream.filter(history);
        return trackStream;
    }

    public Stream<Track> forArtist(Stream<Track> trackStream, Set<Artist> artists) {
        Predicate<Track> artistPredicate = new ArtistPredicate(artists);
        trackStream = trackStream.filter(artistPredicate);
        return trackStream;
    }

    public Stream<Track> forPublished(Stream<Track> trackStream) {
        trackStream = trackStream.filter(published);
        return trackStream;
    }

    public Stream<Track> byRadio(Stream<Track> trackStream) {
        //filer for Genre if needed
        if (radio.getGenres() != null && radio.getGenres().length > 0) {
            trackStream = trackStream.filter(genre);
        }
        if (radio.getStartYear() > 0 && radio.getEndYear() > 0) {
            trackStream = trackStream.filter(published);
        }

        //filter for History
        Set<Track> trackSet = trackStream.collect(Collectors.toSet());  // filter History
        if (trackSet.size() > resultCount) {
            return trackSet.stream();       //result filtered for History
        } else {
            return trackStream;             //result with already used tracks
        }
    }


}
