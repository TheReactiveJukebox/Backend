package de.reactivejukebox.model;

import java.util.List;

/**
 * TrackFeedback is a model for a track feedback containing only ids for user and radio
 */
public class TrackFeedback {

    private int id; //global feedback id
    private int trackId;
    private int songFeedback;
    private int speedFeedback;
    private int dynamicsFeedback;
    private int moodFeedback;



    public TrackFeedback() {
    }

    public TrackFeedback(int id, int trackId, int songFeedback, int speedFeedback, int dynamicsFeedback, int moodFeedback) {
        this.id = id;
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
