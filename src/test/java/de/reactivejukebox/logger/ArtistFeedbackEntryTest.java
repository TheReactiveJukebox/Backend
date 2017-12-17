package de.reactivejukebox.logger;

import de.reactivejukebox.model.ArtistFeedback;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class ArtistFeedbackEntryTest extends EntryTest {
    ArtistFeedback getArtistFeedbackObj() {
        return new ArtistFeedback(33, 0);
    }

    @Test
    public void testArtistField() {
        Entry e = new ArtistFeedbackEntry(getUserObj(), getArtistFeedbackObj());
        String[] s = e.getEntries();
        // Assert
        assertEquals("33", s[EntryCol.ARTIST.ordinal()]);
    }

    @Test
    public void testRatingFieldPositive() {
        ArtistFeedback feedback = getArtistFeedbackObj();
        feedback.setFeedback(1);
        Entry e = new ArtistFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("1", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testRatingFieldNeutral() {
        ArtistFeedback feedback = getArtistFeedbackObj();
        feedback.setFeedback(0);
        Entry e = new ArtistFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("0", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testRatingFieldNegative() {
        ArtistFeedback feedback = getArtistFeedbackObj();
        feedback.setFeedback(-1);
        Entry e = new ArtistFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("-1", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testJsonField() {
        Entry e = new ArtistFeedbackEntry(getUserObj(), getArtistFeedbackObj());
        String[] s = e.getEntries();
        assertNotNull(s[EntryCol.JSON.ordinal()]);
    }
}
