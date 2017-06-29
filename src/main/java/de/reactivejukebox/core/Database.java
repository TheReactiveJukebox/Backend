package de.reactivejukebox.core;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    private static Database instance = null;
    private ComboPooledDataSource dataSource;

    private Database() {
        dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("org.postgresql.Driver");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
            // thrown when class not found?
        }

        // database specifics
        // TODO Move constants
        dataSource.setJdbcUrl("jdbc:postgresql://database:5432/reactivejukebox");
        dataSource.setUser("backend");
        dataSource.setPassword("xxx");

        // connection pool settings
        dataSource.setMinPoolSize(8);
        dataSource.setMaxPoolSize(32);
        dataSource.setAcquireIncrement(8);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public synchronized static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
}
