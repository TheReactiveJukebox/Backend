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

public class SpeedPredicateTest {
    @Test
    private void SpeedTest(){
        String[] radioGenres= new String[1];
        radioGenres[0] = "meta1";
        Radio r = new Radio(1, new User(), radioGenres, 0, 0, 0f, 0f, 0f, 90f, 100f, new LinkedList<Track>(), StrategyType.RANDOM);

        Date d1 = new GregorianCalendar(1990,1,1).getTime();
        Date d2 = new GregorianCalendar(2000,1,1).getTime();
        Date d3 = new GregorianCalendar(1989,1,1).getTime();
        Date d4 = new GregorianCalendar(2001,1,1).getTime();

        Float s1 = 90f;
        Float s2 = 100f;
        Float s3 = 89.99f;
        Float s4 = 100.0001f;

        Track t1 = new Track(1, "Title1", new Artist(), new Album(), "blacover", "blahash", 50, 0, d1, s1, 0.9f, "", "",0,0);
        Track t2 = new Track(2, "Title2", new Artist(), new Album(), "blacover", "blahash", 50, 0, d2, s2, 0.9f, "", "",0,0);
        Track t3 = new Track(1, "Title3", new Artist(), new Album(), "blacover", "blahash", 50, 0, d3, s3, 0.9f, "", "",0,0);
        Track t4 = new Track(2, "Title4", new Artist(), new Album(), "blacover", "blahash", 50, 0, d4, s4, 0.9f, "", "",0,0);
        Track t5 = new Track(2, "Title5", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 0, 0.9f, "", "",0,0);

        Predicate<Track> speedPredicate = new SpeedPredicate(r);

        assertTrue(speedPredicate.test(t1));
        assertTrue(speedPredicate.test(t2));
        assertFalse(speedPredicate.test(t3));
        assertFalse(speedPredicate.test(t4));
        assertFalse(speedPredicate.test(t5));
    }
}
