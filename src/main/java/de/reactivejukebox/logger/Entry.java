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
}
