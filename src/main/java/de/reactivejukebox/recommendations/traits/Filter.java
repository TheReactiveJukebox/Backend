package de.reactivejukebox.recommendations.traits;

import de.reactivejukebox.model.*;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Filter {
    private Predicate<Track> history;
    private Predicate<Track> genre;
    private Radio radio;
    private Collection<Track> upcoming;

    public Filter(Radio radio, Collection<Track> upcoming){
        this(Model.getInstance().getGenres(),Model.getInstance().getHistoryEntries(),radio,upcoming);
    }

    public Filter(Genres genres, HistoryEntries historyEntries, Radio radio, Collection<Track> upcoming){
        this.radio = radio;
        this.upcoming = upcoming;
        history = new HistoryPredicate(historyEntries,radio, upcoming);
        genre = new GenrePredicate(genres, radio);
    }


    public Stream<Track> forGenre(Stream<Track> trackStream){
        trackStream = trackStream.filter(genre);
        return trackStream;
    }

    public Stream<Track> forHistory(Stream<Track> trackStream){
        trackStream = trackStream.filter(history);
        return trackStream;
    }

    public Stream<Track> forArtist(Stream<Track> trackStream, Set<Artist> artists) {
        Predicate<Track> artistPredicate = new ArtistPredicate(artists);
        trackStream = trackStream.filter(artistPredicate);
        return trackStream;
    }

    public Stream<Track> byRadio(Stream<Track> trackStream){
        if(radio.getGenres()!= null && radio.getGenres().length>0){
            trackStream = trackStream.filter(genre);
        }
        trackStream = trackStream.filter(history);
        return trackStream;
    }


}
