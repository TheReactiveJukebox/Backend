package de.reactivejukebox.logger;

import de.reactivejukebox.model.HistoryEntryPlain;
import de.reactivejukebox.model.User;

public class HistoryPostEntry extends Entry {
    public HistoryPostEntry(User user, HistoryEntryPlain historyEntry) {
        super(Event.HISTORY_POST, user);
        setValue(EntryCol.RADIO, historyEntry.getRadioId());
        setValue(EntryCol.SONG, historyEntry.getTrackId());
        setValue(EntryCol.HISTORY, historyEntry.getId());
        setValue(EntryCol.JSON, historyEntry);
    }
}
