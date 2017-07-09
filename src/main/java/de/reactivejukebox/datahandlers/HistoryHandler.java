package de.reactivejukebox.datahandlers;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.HistoryEntries;
import de.reactivejukebox.model.HistoryEntryPlain;
import de.reactivejukebox.model.User;

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

    public void addHistoryEntry(HistoryEntryPlain entry, User user) throws SQLException {
        entry.setUserId(user.getId());
        historyEntries.put(entry);
    }

    //TODO Add methods to get and filter History
}
