package de.reactivejukebox.logger;

import de.reactivejukebox.model.User;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class EntryTest {
    private User getUserObj() {
        User user = new User();
        user.setUserID(1337);
        user.setUsername("Foo");
        return user;
    }

    @Test
    public void testEntryLength() {
        User user = getUserObj();
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
        Entry e = new Entry(Event.USER_LOGIN, getUserObj());
        String[] s = e.getEntry();
        // Assert
        Integer t = new Integer(s[EntryCol.TIMESTAMP.ordinal()]);
        assertTrue(t > 0);
    }

    @Test
    public void testEntryFieldEvent() {
        Event ev = Event.USER_LOGIN;
        Entry e = new Entry(ev, getUserObj());
        // Assert
        assertEquals(e.getEvent(), ev);

        String[] s = e.getEntry();
        // Assert
        assertEquals(s[EntryCol.EVENT.ordinal()], ev.toString());
    }

    @Test
    public void testEntryFieldUser() {
        Entry e = new Entry(Event.USER_LOGIN, getUserObj());
        String[] s = e.getEntry();
        // Assert
        assertEquals(s[EntryCol.USER.ordinal()], "1337");
    }
}
