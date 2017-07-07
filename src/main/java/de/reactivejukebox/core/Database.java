package de.reactivejukebox.core;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    private static final String DB_URL = "jdbc:postgresql://database:5432/reactivejukebox";
    private static final String DB_USER = "backend";
    private static final String DB_PASSWORD = "xxx";

    private static Database instance = null;
    private ComboPooledDataSource dataSource;

    private Database() {
        dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("org.postgresql.Driver");
        } catch (PropertyVetoException e) {
            // - probably thrown when class not found -
            // setDriverClass does nothing but redirect the call to another method of an
            // internal object, where this exception isn't documented. Can't really do
            // anything about this.
            e.printStackTrace();
            System.err.println("This should never happen!");
        }

        // database specifics
        dataSource.setJdbcUrl(DB_URL);
        dataSource.setUser(DB_USER);
        dataSource.setPassword(DB_PASSWORD);

        // connection pool settings
        dataSource.setMinPoolSize(8);
        dataSource.setMaxPoolSize(32);
        dataSource.setAcquireIncrement(8);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Normalization procedure we defined for some of our music metadata in the database.
     * @param str String to normalize
     * @return normalized string
     */
    public String normalize(String str) {
        // convert to lowercase, strip spaces, replace umlauts
        str = str.toLowerCase();
        str = str.replaceAll(" ", "");
        str = str.replaceAll("ö", "o");
        str = str.replaceAll("ü", "u");
        str = str.replaceAll("ä", "a");
        str = str.replaceAll("ß", "s");

        // remove non-ascii characters
        str = str.replaceAll("[^\\x00-\\x7F]", "");

        // remove special characters
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }

    public synchronized static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
}

