package de.reactivejukebox.model;


import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.database.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * TrackFeedbacks is a class containing all the given track feedbacks. It handles all actions concerning adding,
 * receiving or removing track feedback.
 */
public class TrackFeedbacks {
    protected Users users;
    protected Tracks tracks;


    public TrackFeedbacks(Users users, Tracks tracks) {
        this.users = users;
        this.tracks = tracks;
    }

    /**
     * Adds the given feedback to the database or updates a feedback with the same userid, songid and radioid as the
     * given feedback, adds the feedback to the hash map if it is absent and returns the feedback as TrackFeedback object.
     *
     * @param feedback
     * @return
     * @throws SQLException
     */
    public TrackFeedback put(TrackFeedbackPlain feedback) throws SQLException {
        toDB(feedback);
        TrackFeedback newTrackFeedback = get(feedback);
        return newTrackFeedback;
    }

    public TrackFeedback get(int id) throws SQLException {
        return build(fromDB(id));
    }

    public ArrayList<TrackFeedback> getByUserId(int id) throws SQLException {
        ArrayList<TrackFeedback> trackFeedbacks;
        ArrayList<TrackFeedbackPlain> feedbacksPlain;
            feedbacksPlain = this.fromDbByUserId(id);
            trackFeedbacks = build(feedbacksPlain);
        return trackFeedbacks;
    }

    public TrackFeedback get(TrackFeedbackPlain feedback) throws SQLException {
        return get(feedback.getTrackId(),feedback.getUserId());
    }

    public TrackFeedback get(int trackId, int userId)throws SQLException{
        return build(fromDbByTrack(trackId, userId));
    }


    private TrackFeedback build(TrackFeedbackPlain feedback) throws SQLException {
        TrackFeedback newTrackFeedback = new TrackFeedback();

        newTrackFeedback.setUser(users.get(feedback.getUserId()));
        newTrackFeedback.setId(feedback.getId());
        newTrackFeedback.setTrack(tracks.get(feedback.getTrackId()));

        newTrackFeedback.setSongFeedback(feedback.getSongFeedback());
        newTrackFeedback.setSpeedFeedback(feedback.getSpeedFeedback());
        newTrackFeedback.setDynamicsFeedback(feedback.getDynamicsFeedback());
        newTrackFeedback.setMoodFeedback(feedback.getMoodFeedback());

        return newTrackFeedback;
    }

    /**
     * Creates an Arraylist of TrackFeedbacks for a given List of TrackFeedbackPlains. Attributes in the returned list will be
     * the same, as in the given list
     *
     * @param feedbackList the list of TrackFeedbackPlains which shall be converted to TrackFeedbacks
     * @return the matching TrackFeedbacks for the feedbackList
     * @throws SQLException
     */
    private ArrayList<TrackFeedback> build(ArrayList<TrackFeedbackPlain> feedbackList) throws SQLException {
        ArrayList<TrackFeedback> newList = new ArrayList<>();
        Iterator<TrackFeedbackPlain> iterator = feedbackList.listIterator();
        while (iterator.hasNext()) {
            newList.add(build(iterator.next()));
        }
        return newList;
    }

    private ArrayList<TrackFeedbackPlain> fromDbByUserId(int id) throws SQLException {
        ArrayList<TrackFeedbackPlain> feedbacks = new ArrayList<>();
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedback WHERE userid = ? ORDER BY id DESC;");
        getFeedback.setInt(1, id);
        ResultSet rs = getFeedback.executeQuery();
        while (rs.next()) {
            feedbacks.add(this.buildPlain(rs));
        }
        con.close();
        return feedbacks;
    }

    private TrackFeedbackPlain fromDbByTrack(int trackId, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedback WHERE userid = ? " +
                " AND songid = ? ORDER BY id DESC;");
        getFeedback.setInt(1, userId);
        getFeedback.setInt(2, trackId);
        ResultSet rs = getFeedback.executeQuery();
        if (rs.next()) {
            TrackFeedbackPlain result = this.buildPlain(rs);
            con.close();
            return result;
        } else {
            con.close();
            TrackFeedbackPlain result = new TrackFeedbackPlain();
            result.setUserId(userId);
            result.setTrackId(trackId);
            return result;
        }

    }

    private TrackFeedbackPlain fromDB(int id) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatementBuilder stmnt = new PreparedStatementBuilder()
                .select("*")
                .from("feedback")
                .addFilter("Id=?", (query, i) -> query.setInt(i, id));
        PreparedStatement dbQuery = stmnt.prepare(con);
        ResultSet rs = dbQuery.executeQuery();
        if (rs.next()) {
            con.close();
            return (this.buildPlain(rs));
        } else {
            con.close();
            throw new SQLException("TrackFeedback with ID=" + id + " was not found");
        }

    }

    private void toDB(TrackFeedbackPlain feedback) throws SQLException {

        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedback (userid, songid," +
                " feedbacksong, feedbackspeed, feedbackdynamics, feedbackmood) " +
                "VALUES(?, ?, ?, ?, ?, ?) " +
                "ON Conflict (userid, songid) Do " +
                "UPDATE Set (feedbacksong, feedbackspeed, " +
                "feedbackdynamics, feedbackmood, time) = " +
                "(?, ?, ?, ?, CURRENT_TIMESTAMP);");

        addFeedback.setInt(1, feedback.getUserId());
        addFeedback.setInt(2, feedback.getTrackId());
        addFeedback.setInt(3, feedback.getSongFeedback());
        addFeedback.setInt(4, feedback.getSpeedFeedback());
        addFeedback.setInt(5, feedback.getDynamicsFeedback());
        addFeedback.setInt(6, feedback.getMoodFeedback());

        addFeedback.setInt(7, feedback.getSongFeedback());
        addFeedback.setInt(8, feedback.getSpeedFeedback());
        addFeedback.setInt(9, feedback.getDynamicsFeedback());
        addFeedback.setInt(10, feedback.getMoodFeedback());

        addFeedback.executeUpdate();
        con.close();

    }

    private TrackFeedbackPlain buildPlain(ResultSet rs) throws SQLException {
        TrackFeedbackPlain feedback = new TrackFeedbackPlain();
        feedback.setId(rs.getInt("id"));
        feedback.setUserId(rs.getInt("userid"));
        feedback.setTrackId(rs.getInt("songid"));

        feedback.setSongFeedback(rs.getInt("feedbacksong"));
        feedback.setSpeedFeedback(rs.getInt("feedbackspeed"));
        feedback.setDynamicsFeedback(rs.getInt("feedbackdynamics"));
        feedback.setMoodFeedback(rs.getInt("feedbackmood"));


        return feedback;
    }

}