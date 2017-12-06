package de.reactivejukebox.logger;

import de.reactivejukebox.model.User;

public class HistoryDeletetEntry extends Entry {
    public HistoryDeletetEntry(User user, Integer historyId) {
        super(Event.HISTORY_DELETE, user);
        // TODO historyId
    }
}