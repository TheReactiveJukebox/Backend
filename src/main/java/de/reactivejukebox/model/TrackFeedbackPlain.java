package de.reactivejukebox.model;

import java.util.List;

/**
 * TrackFeedbackPlain is a model for a track feedback containing only ids for user and radio
 */
public class TrackFeedbackPlain {

    private int id; //global feedback id
    private int userId;
    private int trackId;
    private int songFeedback;
    private int speedFeedback;
    private int dynamicsFeedback;
    private int moodFeedback;



    public TrackFeedbackPlain() {
    }

    public TrackFeedbackPlain(int id, int userId, int trackId, int songFeedback, int speedFeedback, int dynamicsFeedback, int moodFeedback) {
        this.id = id;
        this.userId = userId;
        this.trackId = trackId;
        this.songFeedback = songFeedback;
        this.speedFeedback = speedFeedback;
        this.dynamicsFeedback = dynamicsFeedback;
        this.moodFeedback = moodFeedback;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getSongFeedback() {
        return songFeedback;
    }

    public void setSongFeedback(int songFeedback) {
        this.songFeedback = songFeedback;
    }

    public int getSpeedFeedback() {
        return speedFeedback;
    }

    public void setSpeedFeedback(int speedFeedback) {
        this.speedFeedback = speedFeedback;
    }

    public int getDynamicsFeedback() {
        return dynamicsFeedback;
    }

    public void setDynamicsFeedback(int dynamicsFeedback) {
        this.dynamicsFeedback = dynamicsFeedback;
    }

    public int getMoodFeedback() {
        return moodFeedback;
    }

    public void setMoodFeedback(int moodFeedback) {
        this.moodFeedback = moodFeedback;
    }

}
