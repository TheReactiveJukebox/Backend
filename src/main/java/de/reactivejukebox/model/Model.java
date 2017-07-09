package de.reactivejukebox.model;


import de.reactivejukebox.database.DatabaseFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class Model {

    private static Model instance;

    private Users users;
    private HistoryEntries historyEntries;
    private Tracks tracks;
    private Artists artists;
    private Albums albums;

    private Model() {
        users = new Users();
        historyEntries = new HistoryEntries(users);
        try (Connection con = DatabaseFactory.getInstance().getDatabase().getConnection()) {
            artists = new Artists(con);
            albums = new Albums(con, artists);
            tracks = new Tracks(con, artists, albums);
        } catch (SQLException e) {
            System.err.println("Could not query music data. Exception:");
            e.printStackTrace(System.err);
            System.err.println("The API is running without any data!");
            artists = new Artists();
            albums = new Albums();
            tracks = new Tracks();
        }
    }

    public static synchronized Model getInstance() {
        if (Model.instance == null) {
            Model.instance = new Model();
        }
        return Model.instance;
    }

    public Users getUsers(){
        return users;
    }

    public HistoryEntries getHistoryEntries(){return historyEntries;}

    public Tracks getTracks() {
        return tracks;
    }

    public Artists getArtists() {
        return artists;
    }

    public Albums getAlbums() {
        return albums;
    }
}
