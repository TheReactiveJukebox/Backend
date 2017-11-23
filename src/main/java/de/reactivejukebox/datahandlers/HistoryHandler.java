package de.reactivejukebox.datahandlers;

import de.reactivejukebox.model.HistoryEntries;
import de.reactivejukebox.model.HistoryEntryPlain;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.User;

import java.sql.SQLException;

public class HistoryHandler {
    private HistoryEntries historyEntries;

    /**
     * adds a new HistoryEntry to the Database
     *
     * @throws SQLException if something goes wrong
     */
    public HistoryHandler() {
        historyEntries = Model.getInstance().getHistoryEntries();
    }

    public HistoryEntryPlain addHistoryEntry(HistoryEntryPlain entry, User user) throws SQLException {
        entry.setUserId(user.getId());
        return historyEntries.put(entry).getPlainObject();
    }

    public void deleteHistoryEntry(Integer historyId, User user) throws SQLException {
        historyEntries.delete(historyId, user);
    }

    //TODO Add methods to get and filter History
}
