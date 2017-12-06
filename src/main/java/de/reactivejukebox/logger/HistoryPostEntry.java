package de.reactivejukebox.logger;

import de.reactivejukebox.model.HistoryEntryPlain;
import de.reactivejukebox.model.User;

public class HistoryPostEntry extends Entry {
    public HistoryPostEntry(User user, HistoryEntryPlain historyEntry) {
        super(Event.HISTORY_POST, user);
        // TODO historyEntry
    }
}
