package de.reactivejukebox.database;


import de.reactivejukebox.model.HistoryEntries;
import de.reactivejukebox.model.Users;

public class DatabaseAccessObject {

    private static DatabaseAccessObject instance;

    private Users users;
    private HistoryEntries historyEntries;



    private DatabaseAccessObject(){
        users = new Users();
        historyEntries = new HistoryEntries(users);
    }


    public static synchronized DatabaseAccessObject getInstance() {
        if (DatabaseAccessObject.instance == null) {
            DatabaseAccessObject.instance = new DatabaseAccessObject();
        }
        return DatabaseAccessObject.instance;
    }

    public Users getUsers(){
        return users;
    }

    public HistoryEntries getHistoryEntries(){return historyEntries;}
}
