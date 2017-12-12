package de.reactivejukebox.logger;

import de.reactivejukebox.model.User;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class UserLoggedInEntryTest extends EntryTest {
    @Test
    public void testUserLoggedInEntry() {
        Entry e = new UserLoggedInEntry(getUserObj().getPlainObject());
        String[] s = e.getEntry();

        // Assert
        assertEquals(s.length, EntryCol.values().length);
    }

}
