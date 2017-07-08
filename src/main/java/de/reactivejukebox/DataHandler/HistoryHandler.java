package de.reactivejukebox.DataHandler;

import de.reactivejukebox.database.DatabaseAccessObject;
import de.reactivejukebox.model.HistoryEntries;
import de.reactivejukebox.model.HistoryEntryPlain;
import de.reactivejukebox.model.User;

import java.sql.SQLException;


public class HistoryHandler {
    private HistoryEntries historyEntries;
    /** adds a new HistoryEntry to the Database
     *
     * @param entry HistoryHandler with Radio and Track id
     * @param user
     * @throws SQLException if something goes wrong
     */
    public HistoryHandler(){
        historyEntries = DatabaseAccessObject.getInstance().getHistoryEntries();
    }

    public void addHistoryEntry(HistoryEntryPlain entry, User user) throws SQLException {
        entry.setUserId(user.getId());
        historyEntries.addEntry(entry);
    }

    //TODO Add methods to get and filter History
}
