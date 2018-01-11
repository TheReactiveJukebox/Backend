package de.reactivejukebox.datahandlers;

import de.reactivejukebox.model.*;

import java.sql.SQLException;


/**
 * The TrackFeedbackHandler is used manage TrackFeedbacks
 */
public class TrackFeedbackHandler {

    private TrackFeedbacks trackFeedbacks;

    public TrackFeedbackHandler() {
        trackFeedbacks = Model.getInstance().getTrackFeedbacks();
    }

    /**
     * Adds a TrackFeedback from a User to the Database
     *
     * @throws SQLException if something goes wrong
     */
    public TrackFeedback addTrackFeedback(TrackFeedback feedback, User user) throws SQLException {
        return trackFeedbacks.put(feedback, user.getId());
    }


}
