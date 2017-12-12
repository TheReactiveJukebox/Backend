package de.reactivejukebox.logger;

import de.reactivejukebox.model.GenreFeedback;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class GenreFeedbackEntryTest extends EntryTest {
    GenreFeedback getGenreFeedbackObj() {
        return new GenreFeedback("genre name", 0);
    }

    @Test
    public void testGenreField() {
        Entry e = new GenreFeedbackEntry(getUserObj(), getGenreFeedbackObj());
        String[] s = e.getEntry();
        // Assert
        assertEquals("genre name", s[EntryCol.GENRE.ordinal()]);
    }

    @Test
    public void testRatingFieldPositiv() {
        GenreFeedback feedback = getGenreFeedbackObj();
        feedback.setFeedback(1);
        Entry e = new GenreFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertEquals("1", s[EntryCol.RATING.ordinal()]);
    }

    @Test
    public void testRatingFieldNeutral() {
        GenreFeedback feedback = getGenreFeedbackObj();
        feedback.setFeedback(0);
        Entry e = new GenreFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertEquals("0", s[EntryCol.RATING.ordinal()]);
    }

    @Test
    public void testRatingFieldNegativ() {
        GenreFeedback feedback = getGenreFeedbackObj();
        feedback.setFeedback(-1);
        Entry e = new GenreFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertEquals("-1", s[EntryCol.RATING.ordinal()]);
    }
}

