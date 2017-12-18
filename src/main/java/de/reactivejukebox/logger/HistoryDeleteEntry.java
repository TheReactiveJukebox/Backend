package de.reactivejukebox.logger;

import de.reactivejukebox.model.User;

public class HistoryDeleteEntry extends Entry {
    public HistoryDeleteEntry(User user, Integer historyId) {
        super(Event.HISTORY_DELETE, user);
        setValue(EntryCol.HISTORY, historyId);
    }
}