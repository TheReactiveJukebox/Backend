package de.reactivejukebox.database;

public class DatabaseProvider {

    private static DatabaseProvider instance;
    private Database db;

    private DatabaseProvider() {
        db = new DatabaseImpl();
    }

    public static DatabaseProvider getInstance() {
        if (instance == null) {
            instance = new DatabaseProvider();
        }
        return instance;
    }

    public Database getDatabase() {
        return db;
    }
}
