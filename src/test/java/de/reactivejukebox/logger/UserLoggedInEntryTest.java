package de.reactivejukebox.logger;

import de.reactivejukebox.model.User;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class UserLoggedInEntryTest {
    @Test
    public void testUserLoggedInEntry() {
        User user = new User();
        user.setUserID(1337);
        user.setUsername("Foo");

        Entry e = new UserLoggedInEntry(user.getPlainObject());
        String[] s = e.getEntry();

        // Assert
        assertEquals(s.length, EntryCol.values().length);
    }

}
