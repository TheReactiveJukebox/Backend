package de.reactivejukebox.database;


import de.reactivejukebox.model.Users;

public class DatabaseAccessObject {

    private static DatabaseAccessObject instance;

    private Users users;



    private DatabaseAccessObject(){
        users = new Users();
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
}
