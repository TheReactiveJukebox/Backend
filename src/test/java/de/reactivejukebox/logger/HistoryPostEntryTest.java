package de.reactivejukebox.logger;

import de.reactivejukebox.model.HistoryEntryPlain;
import de.reactivejukebox.model.User;
import org.testng.annotations.Test;

import java.sql.Timestamp;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class HistoryPostEntryTest extends EntryTest {
    @Test
    public void testHistoryField() {
        Entry e = new HistoryDeleteEntry(getUserObj(), getHistoryEntryPlainObj().getId());
        String[] s = e.getEntry();
        // Assert
        assertEquals("1", s[EntryCol.HISTORY.ordinal()]);
    }

    @Test
    public void testSongField() {
        Entry e = new HistoryPostEntry(getUserObj(), getHistoryEntryPlainObj());
        String[] s = e.getEntry();
        // Assert
        assertEquals("2", s[EntryCol.SONG.ordinal()]);
    }

    @Test
    public void testRadioField() {
        Entry e = new HistoryPostEntry(getUserObj(), getHistoryEntryPlainObj());
        String[] s = e.getEntry();
        // Assert
        assertEquals("3", s[EntryCol.RADIO.ordinal()]);
    }

    @Test
    public void testJsonField() {
        Entry e = new HistoryPostEntry(getUserObj(), getHistoryEntryPlainObj());
        String[] s = e.getEntry();
        assertNotNull(s[EntryCol.JSON.ordinal()]);
    }
}
