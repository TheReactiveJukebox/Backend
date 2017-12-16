package de.reactivejukebox.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.reactivejukebox.model.User;
import de.reactivejukebox.model.UserPlain;

import java.util.StringJoiner;

public class Entry {
    private Event ev;
    private String[] entry = new String[EntryCol.values().length];

    /**
     * Build a head entry
     */
    static Entry getHeadEntry() {
        Entry e = new Entry();
        for (EntryCol col: EntryCol.values()) {
            e.setValue(col, col.toString());
        }
        return e;
    }

    private Entry() {
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
        String s = "";
        if (value != null)
            s = value;
        entry[col.ordinal()] = s;
    }

    void setValue(final EntryCol col, final Long value) {
        String s = "";
        if (value != null)
            s = value.toString();
        entry[col.ordinal()] = s;
    }

    void setValue(final EntryCol col, final Integer value) {
        String s = "";
        if (value != null)
            s = value.toString();
        entry[col.ordinal()] = s;
    }

    void setValue(final EntryCol col, final Float value) {
        String s = "";
        if (value != null)
            s = value.toString();
        entry[col.ordinal()] = s;
    }

    void setValue(final EntryCol col, final Object obj) {
        String s = "";
        if (obj != null) {
            try {
                s = new ObjectMapper().writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        entry[col.ordinal()] = s;
    }

    public Event getEvent() {
        return ev;
    }

    public String[] getEntry() {
        return entry;
    }

    public String getLogString(char delimiter) {
        StringJoiner msg = new StringJoiner(String.valueOf(delimiter));
        for (String colValue : getEntry()) {
            if (colValue == null) {
                msg.add("");
                continue;
            }
            msg.add(colValue);
        }

        return msg.toString();
    }

    public String getLogString() {
        return getLogString(';');
    }
}
