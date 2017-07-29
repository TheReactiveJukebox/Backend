package de.reactivejukebox.recommendations.traits;

import de.reactivejukebox.model.Genres;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class GenrePredicate implements Predicate<Track>{
    private Set<String> excludedGenres;

    public GenrePredicate(Radio radio){
        this(Model.getInstance().getGenres(),radio);
    }

    public GenrePredicate(Genres genres, Radio radio){
        Set<String> selGenres = new HashSet<>();
        if (radio.getGenres()!= null){
            for (String s : radio.getGenres()) {
                selGenres.addAll(genres.getGenre(s));
            }
        }
        List<String> allGenres = genres.genreList();
        excludedGenres = new HashSet<>();
        excludedGenres.addAll(allGenres);
        excludedGenres.removeAll(selGenres);
    }

    @Override
    public boolean test(Track track) {
        return !excludedGenres.stream().anyMatch(track.getGenres()::contains);
    }
}
