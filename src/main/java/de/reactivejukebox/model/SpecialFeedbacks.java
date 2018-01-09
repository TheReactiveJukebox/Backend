package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
    Special Feedbacks is a class that handles all non Track specific Feedback.
 */
public class SpecialFeedbacks {
    Users users;

    public SpecialFeedbacks(Users users) {
        this.users = users;
    }

    /**
     *
     * @param feedback Object for Artist
     * @param userId
     * @return the same feedback Object
     * @throws SQLException
     */
    public ArtistFeedback putArtistFeedback(ArtistFeedback feedback, int userId) throws SQLException {
        toDbArtist(feedback,userId);
        return feedback;
    }
    /**
     *
     * @param feedback Object for Genre
     * @param userId
     * @return the same feedback Object
     * @throws SQLException
     */
    public GenreFeedback putGenreFeedback(GenreFeedback feedback, int userId) throws SQLException {
        toDbGenre(feedback,userId);
        return feedback;
    }
    /**
     *
     * @param feedback Object for Album
     * @param userId
     * @return the same feedback Object
     * @throws SQLException
     */
    public AlbumFeedback putAlbumFeedback(AlbumFeedback feedback, int userId) throws  SQLException {
        toDbAlbum(feedback, userId);
        return feedback;
    }

    public SpeedFeedback putSpeedFeedback(SpeedFeedback feedback, int userId) throws  SQLException {
        toDbSpeed(feedback, userId);
        return feedback;
    }

    public MoodFeedback putMoodFeedback(MoodFeedback feedback, int userId) throws  SQLException {
        toDbMood(feedback, userId);
        return feedback;
    }


    /**
     *
     * @param artistIds list af artist Id's
     * @param userId
     * @return list: feedback Object for each Artist
     * @throws SQLException
     */
    public List<ArtistFeedback> getArtistFeedback(List<Integer> artistIds, int userId) throws SQLException {
        List<ArtistFeedback> result = new ArrayList<>();
        for (Integer a:artistIds) {
            result.add(fromArtistByArtist(a, userId));
        }
        return result;
    }

    /**
     *
     * @param genres list of Genres
     * @param userId
     * @return list: feedback Object for each Genre
     * @throws SQLException
     */
    public List<GenreFeedback> getGenreFeedback(List<String> genres, int userId) throws SQLException {
        List<GenreFeedback> result = new ArrayList<>();
        for (String g:genres) {
            result.add(fromGenreByGenre(g, userId));
        }
        return result;
    }

    /**
     *
     * @param albumIds list of Album Id's
     * @param userId
     * @return list: feedback Object for each Album
     * @throws SQLException
     */
    public List<AlbumFeedback> getAlbumFeedback(List<Integer> albumIds, int userId) throws SQLException {
        List<AlbumFeedback> result = new ArrayList<>();
        for (Integer a:albumIds){
            result.add(fromAlbumByAlbum(a,userId));
        }
        return result;
    }

    public List<SpeedFeedback> getSpeedFeedback(List<Integer> speeds, int userId) throws SQLException {
        List<SpeedFeedback> result = new ArrayList<>();
        for (Integer a:speeds){
            result.add(fromSpeedBySpeed(a,userId));
        }
        return result;
    }

    public List<MoodFeedback> getMoodFeedback(List<Integer> moods, int userId) throws SQLException {
        List<MoodFeedback> result = new ArrayList<>();
        for (Integer a:moods){
            result.add(fromMoodByMood(a,userId));
        }
        return result;
    }

    /**
     *
     * @param artistId artist Id
     * @param userId
     * @return feedback Object forArtist
     * @throws SQLException
     */
    public ArtistFeedback getArtistFeedback(int artistId, int userId) throws SQLException {
        return fromArtistByArtist(artistId, userId);
    }

    /**
     *
     * @param genre Genre
     * @param userId
     * @return eedback Object forGenre
     * @throws SQLException
     */
    public GenreFeedback getGenreFeedback(String genre, int userId) throws SQLException {
        return fromGenreByGenre(genre, userId);
    }

    /**
     *
     * @param albumId Album Id
     * @param userId
     * @return feedback Object for Album
     * @throws SQLException
     */
    public AlbumFeedback getAlbumFeedback(int albumId, int userId) throws SQLException {
        return fromAlbumByAlbum(albumId,userId);
    }

    public SpeedFeedback getSpeedFeedback(int speed, int userId) throws SQLException {
        return fromSpeedBySpeed(speed,userId);
    }

    public MoodFeedback getMoodFeedback(int mood, int userId) throws SQLException {
        return fromMoodByMood(mood,userId);
    }

    public HashMap<Integer, Integer> getArtistFeedback(int userId) throws SQLException {
        return fromArtistByUser(userId);
    }

    public HashMap<Integer, Integer> getAlbumFeedback(int userId) throws SQLException {
        return fromAlbumByUser(userId);
    }

    public HashMap<String, Integer> getGenreFeedback(int userId) throws SQLException {
        return fromGenreByUser(userId);
    }

    public HashMap<Integer, Integer> getSpeedFeedback(int userId)  throws SQLException {
        return fromSpeedByUser(userId);
    }

    public HashMap<Integer, Integer> getMoodFeedback(int userId)  throws SQLException {
        return fromMoodByUser(userId);
    }

    private ArtistFeedback fromArtistByArtist(int artist, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackArtist WHERE userid = ? " +
                " AND artistid = ?;");
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

    private HashMap<Integer, Integer> fromArtistByUser(int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackArtist WHERE userid = ?;");
        getFeedback.setInt(1, userId);
        ResultSet rs = getFeedback.executeQuery();
        HashMap<Integer, Integer> result = new HashMap<>();

        while (rs.next()) {
            result.put(rs.getInt("artistId"), rs.getInt("feedbackArtist"));
        }
        con.close();
        return result;
    }

    private AlbumFeedback fromAlbumByAlbum(int album, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackAlbum WHERE userid = ? " +
                " AND albumid = ?;");
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

    private HashMap<Integer, Integer> fromAlbumByUser(int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackAlbum WHERE userid = ?;");
        getFeedback.setInt(1, userId);
        ResultSet rs = getFeedback.executeQuery();
        HashMap<Integer, Integer> result = new HashMap<>();

        while (rs.next()) {
            result.put(rs.getInt("albumId"), rs.getInt("feedbackAlbum"));
        }
        con.close();
        return result;
    }

    private GenreFeedback fromGenreByGenre(String genre, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackGenre WHERE userid = ? " +
                " AND genre = ?;");
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

    private HashMap<String, Integer> fromGenreByUser(int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackGenre WHERE userid = ?;");
        getFeedback.setInt(1, userId);
        ResultSet rs = getFeedback.executeQuery();
        HashMap<String, Integer> result = new HashMap<>();

        while (rs.next()) {
            result.put(rs.getString("genre"), rs.getInt("feedbackGenre"));
        }
        con.close();
        return result;
    }

    private void toDbGenre(GenreFeedback feedback, int userId) throws SQLException {

        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedbackGenre (userid, " +
                " genre, feedbackGenre) " +
                "VALUES(?, ?, ?) " +
                "ON Conflict (userid, genre) Do " +
                "UPDATE Set feedbackGenre = ?");

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
                "UPDATE Set feedbackArtist = ?;");

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
                "UPDATE Set feedbackAlbum = ?;");

        addFeedback.setInt(1, userId);
        addFeedback.setInt(2, feedback.getAlbum());
        addFeedback.setInt(3, feedback.getFeedback());

        addFeedback.setInt(4, feedback.getFeedback());

        addFeedback.executeUpdate();
        con.close();
    }

    private SpeedFeedback fromSpeedBySpeed(int speed, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackSpeed WHERE userid = ? " +
                " AND speed = ?;");
        getFeedback.setInt(1, userId);
        getFeedback.setInt(2, speed);
        ResultSet rs = getFeedback.executeQuery();
        SpeedFeedback feedback = new SpeedFeedback();
        feedback.setFSpeed(speed);
        if (rs.next()) {
            feedback.setFeedback(rs.getInt("feedbackSpeed"));
        }
        con.close();
        return feedback;
    }

    private HashMap<Integer, Integer> fromSpeedByUser(int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackSpeed WHERE userid = ?;");
        getFeedback.setInt(1, userId);
        ResultSet rs = getFeedback.executeQuery();
        HashMap<Integer, Integer> result = new HashMap<>();

        while (rs.next()) {
            result.put(rs.getInt("Speed"), rs.getInt("feedbackSpeed"));
        }
        con.close();
        return result;
    }

    private void toDbSpeed(SpeedFeedback feedback, int userId) throws SQLException {

        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedbackSpeed (userid, " +
                " Speed, feedbackMood) " +
                "VALUES(?, ?, ?) " +
                "ON Conflict (UserId, Speed) Do " +
                "UPDATE Set feedbackMood = ?;");

        addFeedback.setInt(1, userId);
        addFeedback.setInt(2, feedback.getFSpeed());
        addFeedback.setInt(3, feedback.getFeedback());

        addFeedback.setInt(4, feedback.getFeedback());

        addFeedback.executeUpdate();
        con.close();
    }

    private MoodFeedback fromMoodByMood(int mood, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackMood WHERE userid = ? " +
                " AND mood = ?;");
        getFeedback.setInt(1, userId);
        getFeedback.setInt(2, mood);
        ResultSet rs = getFeedback.executeQuery();
        MoodFeedback feedback = new MoodFeedback();
        feedback.setFMood(mood);
        if (rs.next()) {
            feedback.setFeedback(rs.getInt("feedbackMood"));
        }
        con.close();
        return feedback;
    }

    private HashMap<Integer, Integer> fromMoodByUser(int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedbackMood WHERE userid = ?;");
        getFeedback.setInt(1, userId);
        ResultSet rs = getFeedback.executeQuery();
        HashMap<Integer, Integer> result = new HashMap<>();

        while (rs.next()) {
            result.put(rs.getInt("Mood"), rs.getInt("feedbackMood"));
        }
        con.close();
        return result;
    }

    private void toDbMood(MoodFeedback feedback, int userId) throws SQLException {

        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedbackMood (userid, " +
                " Speed, feedbackMood) " +
                "VALUES(?, ?, ?) " +
                "ON Conflict (UserId, Mood) Do " +
                "UPDATE Set feedbackMood = ?;");

        addFeedback.setInt(1, userId);
        addFeedback.setInt(2, feedback.getFMood());
        addFeedback.setInt(3, feedback.getFeedback());

        addFeedback.setInt(4, feedback.getFeedback());

        addFeedback.executeUpdate();
        con.close();
    }


}
