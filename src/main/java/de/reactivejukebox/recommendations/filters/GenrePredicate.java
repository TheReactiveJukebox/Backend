package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.Genres;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Returns TRUE if the Track belongs to given Genre
 */
public class GenrePredicate implements Predicate<Track> {
    private Set<String> excludedGenres;
    private Set<String> selGenres;

    public GenrePredicate(Radio radio) {
        this(Model.getInstance().getGenres(), radio);
    }

    public GenrePredicate(Genres genres, Radio radio) {
        selGenres = new HashSet<>();
        if (radio.getGenres() != null) {
            for (String s : radio.getGenres()) {
                selGenres.addAll(genres.getGenre(s));
            }
        }
        //List<String> allGenres = genres.genreList();
        //excludedGenres = new HashSet<>();
        //excludedGenres.addAll(allGenres);
        //excludedGenres.removeAll(selGenres);
    }

    @Override
    public boolean test(Track track) {
        return selGenres.stream().anyMatch(track.getGenres()::contains);
    }
}
