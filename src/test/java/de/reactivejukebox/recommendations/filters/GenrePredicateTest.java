package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.filters.GenrePredicate;
import de.reactivejukebox.recommendations.strategies.StrategyType;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Predicate;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class GenrePredicateTest {
    @Test
    private void GenrePredicateTrue() {

        Genres  genres = new Genres();
        genres.put("genre1", "meta1");
        genres.put("genre2", "meta1");
        genres.put("genre4", "meta2");
        genres.put("genre5", "meta2");

        String[] radioGenres= new String[1];
        radioGenres[0] = "meta1";
        Radio r = new Radio(1, new User(), radioGenres, "mood", 1990, 2000, new LinkedList<Track>(), StrategyType.RANDOM);

        Track t1 = new Track(1, "Title1", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date());
        Track t2 = new Track(2, "Title2", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date());
        t1.getGenres().add("genre1");
        t2.getGenres().add("genre2");

        Predicate<Track> genrePred = new GenrePredicate(genres,r);

        assertTrue(genrePred.test(t1));
        assertTrue(genrePred.test(t2));
    }

    @Test
    private void GenrePredicateFalse() {

        Genres  genres = new Genres();
        genres.put("genre1", "meta1");
        genres.put("genre2", "meta1");
        genres.put("genre4", "meta2");
        genres.put("genre5", "meta2");

        String[] radioGenres= new String[1];
        radioGenres[0] = "meta1";
        Radio r = new Radio(1, new User(), radioGenres, "mood", 1990, 2000, new LinkedList<Track>(), StrategyType.RANDOM);

        Track t1 = new Track(1, "Title1", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date());
        Track t2 = new Track(2, "Title2", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date());
        t1.getGenres().add("genre4");
        t2.getGenres().add("genre5");

        Predicate<Track> genrePred = new GenrePredicate(genres,r);

        assertFalse(genrePred.test(t1));
        assertFalse(genrePred.test(t2));


    }
}
