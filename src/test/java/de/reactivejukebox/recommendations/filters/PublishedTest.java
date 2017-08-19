package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.strategies.StrategyType;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.Predicate;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class PublishedTest {

    @Test
    private void Published(){
        String[] radioGenres= new String[1];
        radioGenres[0] = "meta1";
        Radio r = new Radio(1, new User(), radioGenres, "mood", 1990, 2000, new LinkedList<Track>(), StrategyType.RANDOM);

        Date d1 = new Date();
        d1.setYear(1990);
        Date d2 = new Date();
        d2.setYear(2000);
        Date d3 = new Date();
        d3.setYear(1989);
        Date d4 = new Date();
        d4.setYear(2001);

        Track t1 = new Track(1, "Title1", new Artist(), new Album(), "blacover", "blahash", 50, 0, d1);
        Track t2 = new Track(2, "Title2", new Artist(), new Album(), "blacover", "blahash", 50, 0, d2);
        Track t3 = new Track(1, "Title3", new Artist(), new Album(), "blacover", "blahash", 50, 0, d3);
        Track t4 = new Track(2, "Title4", new Artist(), new Album(), "blacover", "blahash", 50, 0, d4);

        Predicate<Track> publishedPredicate = new PublishedPredicate(r);

        assertTrue(publishedPredicate.test(t1));
        assertTrue(publishedPredicate.test(t2));
        assertFalse(publishedPredicate.test(t3));
        assertFalse(publishedPredicate.test(t4));
    }
}
