package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.strategies.StrategyType;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.function.Predicate;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class PublishedTest {

    @Test
    private void Published(){
        String[] radioGenres= new String[1];
        radioGenres[0] = "meta1";
        Radio r = new Radio(1, new User(), radioGenres, 1990, 2000, 0f, 0f, 0f, 0f, 0f, new LinkedList<Track>(), StrategyType.RANDOM);

        Date d1 = new GregorianCalendar(1990,1,1).getTime();
        Date d2 = new GregorianCalendar(2000,1,1).getTime();
        Date d3 = new GregorianCalendar(1989,1,1).getTime();
        Date d4 = new GregorianCalendar(2001,1,1).getTime();

        Track t1 = new Track(1, "Title1", new Artist(), new Album(), "blacover", "blahash", 50, 0, d1, 120, 0.9f);
        Track t2 = new Track(2, "Title2", new Artist(), new Album(), "blacover", "blahash", 50, 0, d2, 120, 0.9f);
        Track t3 = new Track(1, "Title3", new Artist(), new Album(), "blacover", "blahash", 50, 0, d3, 120, 0.9f);
        Track t4 = new Track(2, "Title4", new Artist(), new Album(), "blacover", "blahash", 50, 0, d4, 120, 0.9f);
        Track t5 = new Track(2, "Title5", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);

        Predicate<Track> publishedPredicate = new PublishedPredicate(r);

        assertTrue(publishedPredicate.test(t1));
        assertTrue(publishedPredicate.test(t2));
        assertFalse(publishedPredicate.test(t3));
        assertFalse(publishedPredicate.test(t4));
        assertFalse(publishedPredicate.test(t5));
    }
}
