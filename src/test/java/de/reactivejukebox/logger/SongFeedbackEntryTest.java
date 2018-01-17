package de.reactivejukebox.logger;

import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.TrackFeedback;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class SongFeedbackEntryTest extends EntryTest {
    Track getTrackObj() {
        Track track = new Track();
        track.setId(123);
        return track;
    }

    TrackFeedback getTrackFeedbackObj() {
        TrackFeedback feedback = new TrackFeedback();
        feedback.setTrackId(getTrackObj().getId());
        return feedback;
    }

    @Test
    public void testSongField() {
        Entry e = new SongFeedbackEntry(getUserObj(), getTrackFeedbackObj());
        String[] s = e.getEntries();
        // Assert
        assertEquals("123", s[EntryCol.SONG.ordinal()]);
    }

    @Test
    public void testRatingSongFieldPositive() {
        TrackFeedback feedback = getTrackFeedbackObj();
        feedback.setSongFeedback(1);
        Entry e = new SongFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("1", s[EntryCol.RATING_SONG.ordinal()]);
    }

/*    @Test
    public void testRatingMoodFieldPositive() {
        TrackFeedback feedback = getTrackFeedbackObj();
        feedback.setMoodFeedback(1);
        Entry e = new SongFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("1", s[EntryCol.RATING_MOOD.ordinal()]);
    }

    @Test
    public void testRatingSpeedFieldPositive() {
        TrackFeedback feedback = getTrackFeedbackObj();
        feedback.setSpeedFeedback(1);
        Entry e = new SongFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("1", s[EntryCol.RATING_SPEED.ordinal()]);
    }

    @Test
    public void testRatingDynamicFieldPositive() {
        TrackFeedback feedback = getTrackFeedbackObj();
        feedback.setDynamicsFeedback(1);
        Entry e = new SongFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntries();
        // Assert
        assertEquals("1", s[EntryCol.RATING_DYNAMIC.ordinal()]);
    }
*/
    @Test
    public void testJsonField() {
        Entry e = new SongFeedbackEntry(getUserObj(), getTrackFeedbackObj());
        String[] s = e.getEntries();
        assertNotNull(s[EntryCol.JSON.ordinal()]);
    }
}
