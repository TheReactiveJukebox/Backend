package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;

import java.sql.Connection;
import java.sql.SQLException;

public class IndirectFeedbackEntrys {
    public static IndirectFeedbackPlain put(IndirectFeedbackPlain entry) throws SQLException {
        toDB(entry);
        return entry;
    }

    private static void toDB(IndirectFeedbackPlain entry) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        // TODO implement
    }
}
