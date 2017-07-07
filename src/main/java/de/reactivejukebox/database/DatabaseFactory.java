package de.reactivejukebox.database;

public class DatabaseFactory {

    private static DatabaseFactory instance;
    private Database db;

    private DatabaseFactory() {
        db = new DatabaseImpl();
    }

    public static DatabaseFactory getInstance() {
        if (instance == null) {
            instance = new DatabaseFactory();
        }
        return instance;
    }

    public Database getDatabase() {
        return db;
    }
}
