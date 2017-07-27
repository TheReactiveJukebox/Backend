package de.reactivejukebox.recommendations.strategies.filters;

import de.reactivejukebox.model.Genres;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Track;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;


public class GenreFilter {
    public static Stream<Track> filterGenres(Stream<Track> possibleTracks, String[] input) {
        // getGenres
        Genres genres = Model.getInstance().getGenres();
        Set<String> selGen = Collections.synchronizedSet(new HashSet());
        for (String s : input) {
            selGen.addAll(genres.getGenre(s));
        }
        //filter Tracks
        return possibleTracks.filter(track -> genres.stream().anyMatch(track.getGenres()::contains));
    }
}