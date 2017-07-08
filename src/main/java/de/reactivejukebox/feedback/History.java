package de.reactivejukebox.feedback;

import de.reactivejukebox.database.DatabaseFactory;
import de.reactivejukebox.model.HistoryEntry;
import de.reactivejukebox.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class History {
    protected Connection con;
    /** adds a new HistoryEntry to the Database
     *
     * @param entry History with Radio and Track id
     * @param user
     * @throws SQLException if something goes wrong
     */
    public void addHistoryEntry(HistoryEntry entry, User user) throws SQLException {
        con = DatabaseFactory.getInstance().getDatabase().getConnection();
        PreparedStatement addEntry = con.prepareStatement("INSERT INTO \"history\" (SongId, UserId, RadioId) VALUES ( ?, ?, ?);");
        addEntry.setInt(1, entry.getTrackId());
        addEntry.setInt(2, user.getId());
        addEntry.setInt(3, entry.getRadioId());
        addEntry.executeUpdate();
        con.close();
    }
}
