package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.database.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialFeedbacks {
    Users users;

    public SpecialFeedbacks(Users users) {
        this.users = users;
    }

    public ArtistFeedback putArtistFeedback(ArtistFeedback feedback, int userId) throws SQLException {
        toDbArtist(feedback,userId);
        return feedback;
    }

    public GenreFeedback putGenreFeedback(GenreFeedback feedback, int userId) throws SQLException {
        toDbGenre(feedback,userId);
        return feedback;
    }

    public AlbumFeedback putAlbumFeedback(AlbumFeedback feedback, int userId) throws  SQLException {
        toDbAlbum(feedback, userId);
        return feedback;
    }

    public List<ArtistFeedback> getArtistFeedback(List<Integer> artistIds, int userId) throws SQLException {
        List<ArtistFeedback> result = new ArrayList<>();
        for (Integer a:artistIds) {
            result.add(fromDbByArtist(a, userId));
        }
        return result;
    }

    public List<GenreFeedback> getGenreFeedback(List<String> genres, int userId) throws SQLException {
        List<GenreFeedback> result = new ArrayList<>();
        for (String g:genres) {
            result.add(fromDbByGenre(g, userId));
        }
        return result;
    }

    public List<AlbumFeedback> getAlbumFeedback(List<Integer> albumIds, int userId) throws SQLException {
        List<AlbumFeedback> result = new ArrayList<>();
        for (Integer a:albumIds){
            result.add(fromDbByAlbum(a,userId));
        }
        return result;
    }

    private ArtistFeedback fromDbByArtist(int artist, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackArtist WHERE userid = ? " +
                " AND artistid = ? ORDER BY id DESC;");
        getFeedback.setInt(1, userId);
        getFeedback.setInt(2, artist);
        ResultSet rs = getFeedback.executeQuery();
        ArtistFeedback feedback = new ArtistFeedback();
        feedback.setArtist(artist);
        if (rs.next()) {
            feedback.setFeedback(rs.getInt("feedbackArtist"));
        }
        con.close();
        return feedback;
    }

    private AlbumFeedback fromDbByAlbum(int album, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackAlbum WHERE userid = ? " +
                " AND albumid = ? ORDER BY id DESC;");
        getFeedback.setInt(1, userId);
        getFeedback.setInt(2, album);
        ResultSet rs = getFeedback.executeQuery();
        AlbumFeedback feedback = new AlbumFeedback();
        feedback.setAlbum(album);
        if (rs.next()) {
            feedback.setFeedback(rs.getInt("feedbackAlbum"));
        }
        con.close();
        return feedback;
    }

    private GenreFeedback fromDbByGenre(String genre, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackGenre WHERE userid = ? " +
                " AND genre = ? ORDER BY id DESC;");
        getFeedback.setInt(1, userId);
        getFeedback.setString(2, genre);
        ResultSet rs = getFeedback.executeQuery();
        GenreFeedback feedback = new GenreFeedback();
        feedback.setGenre(genre);
        if (rs.next()) {
            feedback.setFeedback(rs.getInt("feedbackGenre"));
        }
        con.close();
        return feedback;
    }

    private void toDbGenre(GenreFeedback feedback, int userId) throws SQLException {

        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedbackGenre (userid, " +
                " genre, feedbackGenre) " +
                "VALUES(?, ?, ?) " +
                "ON Conflict (userid, genre) Do " +
                "UPDATE Set (feedbackGenre);" +
                "(?);");

        addFeedback.setInt(1, userId);
        addFeedback.setString(2, feedback.getGenre());
        addFeedback.setInt(3, feedback.getFeedback());

        addFeedback.setInt(4,feedback.getFeedback());

        addFeedback.executeUpdate();
        con.close();
    }

    private void toDbArtist(ArtistFeedback feedback, int userId) throws SQLException {

        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedbackArtist (userid, " +
                " ArtistId, feedbackArtist) " +
                "VALUES(?, ?, ?) " +
                "ON Conflict (userid, ArtistId) Do " +
                "UPDATE Set (feedbackArtist);" +
                "(?);");

        addFeedback.setInt(1, userId);
        addFeedback.setInt(2, feedback.getArtist());
        addFeedback.setInt(3, feedback.getFeedback());

        addFeedback.setInt(4, feedback.getFeedback());

        addFeedback.executeUpdate();
        con.close();
    }

    private void toDbAlbum(AlbumFeedback feedback, int userId) throws SQLException {

        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedbackAlbum (userid, " +
                " AlbumId, feedbackAlbum) " +
                "VALUES(?, ?, ?) " +
                "ON Conflict (userid, AlbumId) Do " +
                "UPDATE Set (feedbackAlbum);" +
                "(?);");

        addFeedback.setInt(1, userId);
        addFeedback.setInt(2, feedback.getAlbum());
        addFeedback.setInt(3, feedback.getFeedback());

        addFeedback.setInt(4, feedback.getFeedback());

        addFeedback.executeUpdate();
        con.close();
    }

}
