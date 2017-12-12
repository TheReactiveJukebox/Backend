package de.reactivejukebox.logger;

import de.reactivejukebox.model.IndirectFeedbackPlain;
import de.reactivejukebox.model.User;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class ActionFeedbackEntryTest extends EntryTest {
    IndirectFeedbackPlain getIndirectFeedbackObj() {
        IndirectFeedbackPlain f = new IndirectFeedbackPlain();
        f.setRadioId(1);
        f.setUserId(USER_ID);
        f.setTrackId(3);
        return f;
    }

    @Test
    public void testRadioField() {
        IndirectFeedbackPlain feedback = getIndirectFeedbackObj();
        feedback.setFeedbackName("SKIP");
        Entry e = new ActionFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertTrue(feedback.isValid());
        assertEquals("1", s[EntryCol.RADIO.ordinal()]);
    }

    @Test
    public void testSongField() {
        IndirectFeedbackPlain feedback = getIndirectFeedbackObj();
        feedback.setFeedbackName("SKIP");
        Entry e = new ActionFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertTrue(feedback.isValid());
        assertEquals("3", s[EntryCol.SONG.ordinal()]);
    }

    /**
     * skip a song after 23 seconds
     */
    @Test
    public void testActionSkipField() {
        IndirectFeedbackPlain feedback = getIndirectFeedbackObj();
        feedback.setFeedbackName("SKIP");
        feedback.setPosition(23);
        Entry e = new ActionFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertTrue(feedback.isValid());
        assertEquals("SKIP", s[EntryCol.USER_ACTION.ordinal()]);
        assertEquals("23", s[EntryCol.SONG_RUNTIME.ordinal()]);
    }

    @Test
    public void testActionMultiSkipField() {
        IndirectFeedbackPlain feedback = getIndirectFeedbackObj();
        feedback.setFeedbackName("MULTI_SKIP");
        feedback.setToTrackId(5);
        Entry e = new ActionFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertTrue(feedback.isValid());
        assertEquals("MULTI_SKIP", s[EntryCol.USER_ACTION.ordinal()]);
        assertEquals("5", s[EntryCol.SONG_FORWARD.ordinal()]);
    }

    @Test
    public void testActionDeleteField() {
        IndirectFeedbackPlain feedback = getIndirectFeedbackObj();
        feedback.setFeedbackName("DELETE");
        Entry e = new ActionFeedbackEntry(getUserObj(), feedback);
        String[] s = e.getEntry();
        // Assert
        assertTrue(feedback.isValid());
        assertEquals("DELETE", s[EntryCol.USER_ACTION.ordinal()]);
    }
}
