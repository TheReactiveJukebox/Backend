package de.reactivejukebox.logger;

import de.reactivejukebox.model.ArtistFeedback;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class ArtistFeedbackEntryTest extends EntryTest {
    ArtistFeedback getArtistFeedbackObj() {
        return new ArtistFeedback(33, 0);
    }

    @Test
    public void testArtistField() {
        Entry e = new ArtistFeedbackEntry(getUserObj(), getArtistFeedbackObj());
        String[] s = e.getEntry();
        // Assert
        assertEquals("33", s[EntryCol.ARTIST.ordinal()]);
    }

    @Test
    public void testRatingFieldPositiv() {
        ArtistFeedback feedback = getArtistFeedbackObj();
        feedback.setFeedback(1);
        Entry e = new ArtistFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertEquals("1", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testRatingFieldNeutral() {
        ArtistFeedback feedback = getArtistFeedbackObj();
        feedback.setFeedback(0);
        Entry e = new ArtistFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertEquals("0", s[EntryCol.RATING_SONG.ordinal()]);
    }

    @Test
    public void testRatingFieldNegativ() {
        ArtistFeedback feedback = getArtistFeedbackObj();
        feedback.setFeedback(-1);
        Entry e = new ArtistFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertEquals("-1", s[EntryCol.RATING_SONG.ordinal()]);
    }
}
