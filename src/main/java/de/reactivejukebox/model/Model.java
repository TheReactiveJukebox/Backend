package de.reactivejukebox.model;


import de.reactivejukebox.database.DatabaseProvider;

import java.sql.Connection;
import java.sql.SQLException;

public class Model {

    private static Model instance;

    private Users users;
    private HistoryEntries historyEntries;
    private Tracks tracks;
    private Artists artists;
    private Albums albums;
    private Radios radios;
    private TrackFeedbacks trackFeedbacks;
    private Genres genres;
    private Playlists playlists;
    private SpecialFeedbacks specialFeedbacks;
    private IndirectFeedbackEntries indirectFeedbackEntries;

    private Model() throws ModelException{
        users = new Users();
        try (Connection con = DatabaseProvider.getInstance().getDatabase().getConnection()) {
            genres = new Genres(con);
            artists = new Artists(con);
            albums = new Albums(con, artists);
            tracks = new Tracks(con, artists, albums);
            con.close();
        } catch (SQLException e) {
            System.err.println("Could not query music data. Exception:");
            e.printStackTrace(System.err);
            System.err.println("The API is running without any data!");
            artists = new Artists();
            albums = new Albums();
            tracks = new Tracks();
            throw new ModelException();
        }
        radios = new Radios(users);
        trackFeedbacks = new TrackFeedbacks(users, tracks);
        specialFeedbacks = new SpecialFeedbacks(users);
        historyEntries = new HistoryEntries(users, tracks, radios);
        playlists = new Playlists();
        indirectFeedbackEntries = new IndirectFeedbackEntries();

    }

    public static synchronized Model getInstance() {
        if (Model.instance == null) {
            try {
                Model.instance = new Model();
            }catch (ModelException e){
                System.err.println("Could build Model returning empty one which is replaced as soon as the database responds");
                return null;
            }
        }
        return Model.instance;
    }

    public Users getUsers() {
        return users;
    }

    public HistoryEntries getHistoryEntries() {
        return historyEntries;
    }

    public Tracks getTracks() {
        return tracks;
    }

    public Artists getArtists() {
        return artists;
    }

    public Albums getAlbums() {
        return albums;
    }

    public Radios getRadios() {
        return radios;
    }

    public Genres getGenres() {
        return genres;
    }

    public TrackFeedbacks getTrackFeedbacks() {
        return trackFeedbacks;
    }

    public SpecialFeedbacks getSpecialFeedbacks() {
        return specialFeedbacks;
    }

    public Playlists getPlaylists() {
        return playlists;
    }

    public IndirectFeedbackEntries getIndirectFeedbackEntries() { return indirectFeedbackEntries; }
}
