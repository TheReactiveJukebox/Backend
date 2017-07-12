package de.reactivejukebox.datahandlers;

import de.reactivejukebox.model.*;

import java.sql.SQLException;

public class HistoryHandler {
    private HistoryEntries historyEntries;
    /** adds a new HistoryEntry to the Database
     *
     * @throws SQLException if something goes wrong
     */
    public HistoryHandler(){
        historyEntries = Model.getInstance().getHistoryEntries();
    }

    public HistoryEntry addHistoryEntry(HistoryEntryPlain entry, User user) throws SQLException {
        entry.setUserId(user.getId());
        return historyEntries.put(entry);
    }

    public void deleteHistoryEntry(Integer historyId) throws SQLException {
        historyEntries.delete(historyId);
    }

    //TODO Add methods to get and filter History
}
