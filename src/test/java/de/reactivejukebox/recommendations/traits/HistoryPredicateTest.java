package de.reactivejukebox.recommendations.traits;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.strategies.StrategyType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Predicate;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class HistoryPredicateTest {

    Track t1;
    Track t2;
    Track t3;
    Track t4;
    Track t5;
    Radio r;
    ArrayList<Track> upcoming;
    HistoryEntries history;

    @BeforeClass
    private void setUp() throws SQLException {

        t1 = new Track(1, "Title1", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date());
        t2 = new Track(2, "Title2", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date());
        t3 = new Track(3, "Title3", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date());
        t4 = new Track(4, "Title4", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date());
        t5 = new Track(5, "Title5", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date());



        User user = new User();
        user.setId(1);
        r = new Radio(1, user, new String[0], "mood", 1990, 2000, new LinkedList<>(), StrategyType.RANDOM);
        ArrayList<HistoryEntry> historyList = new ArrayList();

        HistoryEntry h1 = new HistoryEntry(1, t1, r, user, new Timestamp(10));
        HistoryEntry h2 = new HistoryEntry(2, t2, r, user, new Timestamp(10));

        historyList.add(h1);
        historyList.add(h2);

        upcoming = new ArrayList<>();
        upcoming.add(t3);

        history = Mockito.mock(HistoryEntries.class);
        Mockito.when(history.getListByUserId(1)).thenReturn(historyList);
    }

    @Test
    private void HistoryPredicateTrue(){
        Predicate<Track> historyPredicate = new HistoryPredicate(history,r,upcoming);

        assertTrue(historyPredicate.test(t4));
        assertTrue(historyPredicate.test(t5));
    }

    @Test
    private void HistoryPredicateFalse(){
        Predicate<Track> historyPredicate = new HistoryPredicate(history,r,upcoming);

        assertFalse(historyPredicate.test(t1));
        assertFalse(historyPredicate.test(t2));
        assertFalse(historyPredicate.test(t3));
    }

}
