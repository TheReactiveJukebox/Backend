package de.reactivejukebox.logger;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class UserRegisterEntryTest extends EntryTest {
    @Test
    public void testUserLoggedInEntry() {
        Entry e = new UserRegisterEntry(getUserObj().getPlainObject());
        String[] s = e.getEntry();

        // Assert
        assertEquals(EntryCol.values().length, s.length);
    }
}
