package de.reactivejukebox.logger;

import de.reactivejukebox.model.GenreFeedback;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class GenreFeedbackEntryTest extends EntryTest {
    GenreFeedback getGenreFeedbackObj() {
        return new GenreFeedback("genre name", 0);
    }

    @Test
    public void testGenreField() {
        Entry e = new GenreFeedbackEntry(getUserObj(), getGenreFeedbackObj());
        String[] s = e.getEntries();
        // Assert
        assertEquals("genre name", s[EntryCol.GENRE.ordinal()]);
    }

    @Test
    public void testRatingFieldPositive() {
        GenreFeedback feedback = getGenreFeedbackObj();
        feedback.setFeedback(1);
        Entry e = new GenreFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("1", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testRatingFieldNeutral() {
        GenreFeedback feedback = getGenreFeedbackObj();
        feedback.setFeedback(0);
        Entry e = new GenreFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("0", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testRatingFieldNegative() {
        GenreFeedback feedback = getGenreFeedbackObj();
        feedback.setFeedback(-1);
        Entry e = new GenreFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("-1", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testJsonField() {
        Entry e = new GenreFeedbackEntry(getUserObj(), getGenreFeedbackObj());
        String[] s = e.getEntries();
        assertNotNull(s[EntryCol.JSON.ordinal()]);
    }
}

