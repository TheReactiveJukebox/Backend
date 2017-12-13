package de.reactivejukebox.logger;

import de.reactivejukebox.model.HistoryEntryPlain;
import de.reactivejukebox.model.User;
import org.testng.annotations.Test;

import java.sql.Timestamp;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class EntryTest {
    final static int USER_ID = 1337;
    final static String USERNAME = "Foo";

    User getUserObj() {
        User user = new User();
        user.setUserID(USER_ID);
        user.setUsername(USERNAME);
        return user;
    }

    HistoryEntryPlain getHistoryEntryPlainObj() {
        return new HistoryEntryPlain(1, 2, 3, USER_ID, new Timestamp(0));
    }

    /**
     * check the filed count of a entry
     */
    @Test
    public void testEntryLength() {
        User user = getUserObj();
        Entry e = new Entry(Event.USER_LOGIN, user);
        String[] s = e.getEntry();
        // Assert
        assertEquals(EntryCol.values().length, s.length);

        e = new Entry(Event.USER_LOGIN, user.getPlainObject());
        s = e.getEntry();
        // Assert
        assertEquals(EntryCol.values().length, s.length);
    }

    /**
     * Check timestamp
     */
    @Test
    public void testFieldTime() {
        Entry e = new Entry(Event.USER_LOGIN, getUserObj());
        String[] s = e.getEntry();
        // Assert
        Integer t = new Integer(s[EntryCol.TIMESTAMP.ordinal()]);
        assertTrue(t > 0);
    }

    /**
     * Check string of a entry
     */
    @Test
    public void testLogEntry() {
        final Character delimiter = ';';
        Entry e = new Entry(Event.USER_LOGIN, getUserObj());
        String logEntry = e.getLogString(delimiter);

        StringBuilder expectedEntry = new StringBuilder();
        for (EntryCol col : EntryCol.values()) {
            switch (col) {
                case USER:
                    expectedEntry.append(String.valueOf(USER_ID));
                    break;
                case TIMESTAMP:
                    expectedEntry.append(e.getEntry()[EntryCol.TIMESTAMP.ordinal()]);
                    break;
                case EVENT:
                    expectedEntry.append(e.getEvent().toString());
                    break;
            }
            expectedEntry.append(delimiter);
        }
        // Assert
        assertEquals(expectedEntry.toString(), logEntry);
    }

    @Test
    public void testFieldEvent() {
        final Event ev = Event.USER_LOGIN;
        Entry e = new Entry(ev, getUserObj());
        // Assert
        assertEquals(e.getEvent(), ev);

        String[] s = e.getEntry();
        // Assert
        assertEquals(ev.toString(), s[EntryCol.EVENT.ordinal()]);
    }

    @Test
    public void testFieldUser() {
        Entry e = new Entry(Event.USER_LOGIN, getUserObj());
        String[] s = e.getEntry();
        // Assert
        assertEquals(String.valueOf(USER_ID), s[EntryCol.USER.ordinal()]);
    }
}
