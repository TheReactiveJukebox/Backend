package de.reactivejukebox.logger;

import de.reactivejukebox.model.HistoryEntryPlain;
import de.reactivejukebox.model.User;
import org.testng.annotations.Test;

import java.sql.Timestamp;

import static org.testng.AssertJUnit.assertEquals;

public class HistoryDeleteEntryTest {
    private User getUserObj() {
        User user = new User();
        user.setUserID(1337);
        user.setUsername("Foo");
        return user;
    }

    private HistoryEntryPlain getHistoryEntryPlainObj() {
        return new HistoryEntryPlain(1, 2, 3, 4, new Timestamp(0));
    }

    @Test
    public void testHistoryField() {
        Entry e = new HistoryDeleteEntry(getUserObj(), getHistoryEntryPlainObj().getId());
        String[] s = e.getEntry();
        // Assert
        assertEquals(s[EntryCol.HISTORY.ordinal()], "1");
    }
}
