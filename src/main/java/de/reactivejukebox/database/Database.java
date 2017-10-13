package de.reactivejukebox.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {
    Connection getConnection() throws SQLException;

    String normalize(String str);
}

