package de.reactivejukebox.logger;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class HistoryDeleteEntryTest extends EntryTest {
    @Test
    public void testHistoryField() {
        Entry e = new HistoryDeleteEntry(getUserObj(), getHistoryEntryPlainObj().getId());
        String[] s = e.getEntries();
        // Assert
        assertEquals("1", s[EntryCol.HISTORY.ordinal()]);
    }
}
