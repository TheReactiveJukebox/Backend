package de.reactivejukebox.logger;

import de.reactivejukebox.model.AlbumFeedback;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class AlbumFeedbackEntryTest extends EntryTest {

    AlbumFeedback getAlbumFeedbackObj() {
        AlbumFeedback feedback = new AlbumFeedback(33, 0);
        return feedback;
    }

    @Test
    public void testAlbumField() {
        Entry e = new AlbumFeedbackEntry(getUserObj(), getAlbumFeedbackObj());
        String[] s = e.getEntries();
        // Assert
        assertEquals("33", s[EntryCol.ALBUM.ordinal()]);
    }

    @Test
    public void testRatingFieldPositive() {
        AlbumFeedback feedback = getAlbumFeedbackObj();
        feedback.setFeedback(1);
        Entry e = new AlbumFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("1", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testRatingFieldNeutral() {
        AlbumFeedback feedback = getAlbumFeedbackObj();
        feedback.setFeedback(0);
        Entry e = new AlbumFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("0", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testRatingFieldNegative() {
        AlbumFeedback feedback = getAlbumFeedbackObj();
        feedback.setFeedback(-1);
        Entry e = new AlbumFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("-1", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testJsonField() {
        Entry e = new AlbumFeedbackEntry(getUserObj(), getAlbumFeedbackObj());
        String[] s = e.getEntries();
        assertNotNull(s[EntryCol.JSON.ordinal()]);
    }
}
