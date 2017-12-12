package de.reactivejukebox.logger;

import de.reactivejukebox.model.AlbumFeedback;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class AlbumFeedbackEntryTest extends EntryTest {

    AlbumFeedback getAlbumFeedbackObj() {
        AlbumFeedback feedback = new AlbumFeedback(33, 0);
        return feedback;
    }

    @Test
    public void testAlbumField() {
        Entry e = new AlbumFeedbackEntry(getUserObj(), getAlbumFeedbackObj());
        String[] s = e.getEntry();
        // Assert
        assertEquals("33", s[EntryCol.ALBUM.ordinal()]);
    }

    @Test
    public void testRatingFieldPositiv() {
        AlbumFeedback feedback = getAlbumFeedbackObj();
        feedback.setFeedback(1);
        Entry e = new AlbumFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertEquals("1", s[EntryCol.RATING.ordinal()]);
    }

    @Test
    public void testRatingFieldNeutral() {
        AlbumFeedback feedback = getAlbumFeedbackObj();
        feedback.setFeedback(0);
        Entry e = new AlbumFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertEquals("0", s[EntryCol.RATING.ordinal()]);
    }

    @Test
    public void testRatingFieldNegativ() {
        AlbumFeedback feedback = getAlbumFeedbackObj();
        feedback.setFeedback(-1);
        Entry e = new AlbumFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertEquals("-1", s[EntryCol.RATING.ordinal()]);
    }
}
