package de.reactivejukebox.logger;

import de.reactivejukebox.model.User;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class EntryTest {
    @Test
    public void testEntryLength() {
        User user = new User();
        user.setUserID(1337);
        user.setUsername("Foo");

        Entry e = new Entry(Event.USER_LOGIN, user);
        String[] s = e.getEntry();
        // Assert
        assertEquals(s.length, EntryCol.values().length);

        e = new Entry(Event.USER_LOGIN, user.getPlainObject());
        s = e.getEntry();
        // Assert
        assertEquals(s.length, EntryCol.values().length);
    }

    @Test
    public void testEntryFieldTime() {
        User user = new User();
        user.setUserID(1337);
        user.setUsername("Foo");

        Entry e = new Entry(Event.USER_LOGIN, user);
        String[] s = e.getEntry();
        // Assert
        Integer t = new Integer(s[EntryCol.TIMESTAMP.ordinal()]);
        assertTrue(t > 0);
    }

    @Test
    public void testEntryFieldEvent() {
        Event ev = Event.USER_LOGIN;

        User user = new User();
        user.setUserID(1337);
        user.setUsername("Foo");

        Entry e = new Entry(ev, user);
        // Assert
        assertEquals(e.getEvent(), ev);

        String[] s = e.getEntry();
        // Assert
        assertEquals(s[EntryCol.EVENT.ordinal()], ev.toString());
    }

    @Test
    public void testEntryFieldUser() {
        int userId = 1337;

        User user = new User();
        user.setUserID(userId);
        user.setUsername("Foo");

        Entry e = new Entry(Event.USER_LOGIN, user);
        String[] s = e.getEntry();
        // Assert
        assertEquals(s[EntryCol.USER.ordinal()], String.valueOf(userId));
    }
}
