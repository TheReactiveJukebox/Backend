package de.reactivejukebox.logger;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class UserLoggedOutEntryTest extends EntryTest {
    @Test
    public void testUserLoggedInEntry() {
        Entry e = new UserLoggedOutEntry(getUserObj().getPlainObject());
        String[] s = e.getEntries();

        // Assert
        assertEquals(EntryCol.values().length, s.length);
    }
}
