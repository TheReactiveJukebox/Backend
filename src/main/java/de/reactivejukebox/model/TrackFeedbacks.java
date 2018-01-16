package de.reactivejukebox.model;


import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.database.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
    public TrackFeedback put(TrackFeedback feedback, int userId) throws SQLException {
        toDB(feedback, userId);
        return fromDbByTrack(feedback.getTrackId(),userId);
    }

    public TrackFeedback get(int id) throws SQLException {
        return fromDB(id);
    }

    public HashMap <Integer, Integer> getByUserId(int id) throws SQLException {
        HashMap<Integer, Integer> trackFeedbacks = new HashMap<>();
        ArrayList<TrackFeedback> feedbacksPlain;
            feedbacksPlain = this.fromDbByUserId(id);
        for (TrackFeedback f: feedbacksPlain) {
            trackFeedbacks.put(f.getTrackId(),f.getSongFeedback());
        }
        return trackFeedbacks;
    }

    public TrackFeedback get(int trackId, int userId)throws SQLException{
        return fromDbByTrack(trackId, userId);
    }


      private ArrayList<TrackFeedback> fromDbByUserId(int id) throws SQLException {
        ArrayList<TrackFeedback> feedbacks = new ArrayList<>();
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

    private TrackFeedback fromDbByTrack(int trackId, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedback WHERE userid = ? " +
                " AND songid = ? ORDER BY id DESC;");
        getFeedback.setInt(1, userId);
        getFeedback.setInt(2, trackId);
        ResultSet rs = getFeedback.executeQuery();
        if (rs.next()) {
            TrackFeedback result = this.buildPlain(rs);
            con.close();
            return result;
        } else {
            con.close();
            TrackFeedback result = new TrackFeedback();
            result.setTrackId(trackId);
            return result;
        }

    }

    private TrackFeedback fromDB(int id) throws SQLException {
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

    private void toDB(TrackFeedback feedback, int userId) throws SQLException {

        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedback (userid, songid," +
                " feedbacksong) " +
                "VALUES(?, ?, ?) " +
                "ON Conflict (userid, songid) Do " +
                "UPDATE Set (feedbacksong, time) = " +
                "(?, CURRENT_TIMESTAMP);");

        addFeedback.setInt(1, userId);
        addFeedback.setInt(2, feedback.getTrackId());
        addFeedback.setInt(3, feedback.getSongFeedback());

        addFeedback.setInt(4, feedback.getSongFeedback());

        addFeedback.executeUpdate();
        con.close();

    }

    private TrackFeedback buildPlain(ResultSet rs) throws SQLException {
        TrackFeedback feedback = new TrackFeedback();
        feedback.setId(rs.getInt("id"));
        feedback.setTrackId(rs.getInt("songid"));

        feedback.setSongFeedback(rs.getInt("feedbacksong"));


        return feedback;
    }

}