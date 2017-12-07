package de.reactivejukebox.logger;

import de.reactivejukebox.model.User;
import de.reactivejukebox.model.UserPlain;

public class Entry {
    private Event ev;
    private String[] entry = new String[EntryCol.values().length];


    // baue Kopf eintrag zusammen
    static String[] getHead() {
        String[] result = new String[EntryCol.values().length];
        for (EntryCol col: EntryCol.values()) {
            result[col.ordinal()] = col.toString();
        }
        return result;
    }

    public Entry(final Event ev, final User user) {
        this.ev = ev;
        setValue(EntryCol.EVENT, ev.toString());
        setValue(EntryCol.USER, user.getId());
        long unixTime = System.currentTimeMillis() / 1000L;
        setValue(EntryCol.TIMESTAMP, unixTime);
    }

    public Entry(final Event ev, final UserPlain user) {
        this.ev = ev;
        setValue(EntryCol.EVENT, ev.toString());
        setValue(EntryCol.USER, user.getId());
        long unixTime = System.currentTimeMillis() / 1000L;
        setValue(EntryCol.TIMESTAMP, unixTime);
    }
    }

    void setValue(final EntryCol col, final String value) {
        entry[col.ordinal()] = value;
    }

    void setValue(final EntryCol col, final long value) {
        entry[col.ordinal()] = String.valueOf(value);
    }

    public Event getEvent() {
        return ev;
    }

    public String[] getEntry() {
        return entry;
    }

    public String getLogString(char delimiter) {
        if (!isValid()) {
            // TODO throw new Entry.IsInvalid();
        }

        StringBuilder msg = new StringBuilder();
        for (String colValue : getEntry()) {
            msg.append(colValue);
            msg.append(delimiter);
        }

        return msg.toString();
    }

    public String getLogString() {
        return getLogString(';');
    }

    private boolean isColUnset(final EntryCol col) {
        // TODO implement
        // entry[col.ordinal()] != ""
        return false;
    }

    private boolean allCollumesSet(final EntryCol[] cols) {
        // do check nothing
        if (cols == null)
            return true;

        for (EntryCol col : cols) {
            if (isColUnset(col))
                return false;
        }
        return true;
    }

    public boolean isValid() {
        // TODO tests
        // entry.length == EntryCol.values().length
        EntryCol[] cols = null;
        switch (ev) {
            case USER_LOGIN:
                break;
            case RADIO_START:
                cols = new EntryCol[]{EntryCol.USER};
                break;
            // TODO implement
            default:
                throw new AssertionError("Unknown Event " + ev.toString());
        }
        return allCollumesSet(cols);
    }


    public class IsInvalid extends Exception {
    }

}
