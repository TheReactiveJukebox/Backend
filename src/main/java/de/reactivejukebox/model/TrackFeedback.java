package de.reactivejukebox.model;

import java.io.Serializable;
import java.util.List;

/**
 * The TrackFeedback class is a model for a single track feedback
 */
public class TrackFeedback implements Serializable {

    private int id; //global feedback id
    private User user;
    private Radio radio;
    private Track track;

    private int songFeedback;
    private int speedFeedback;
    private int dynamicsFeedback;
    private int moodFeedback;


    public TrackFeedback() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Radio getRadio() {
        return radio;
    }

    public void setRadio(Radio radio) {
        this.radio = radio;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
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


    /**
     * Creates a matching TrackFeedbackPlain object.
     *
     * @return the matching TrackFeedbackPlain object with the same attributes as this TrackFeedback object
     */
    public TrackFeedbackPlain getPlainObject() {
        TrackFeedbackPlain plainFeedback = new TrackFeedbackPlain(this.getId(), this.getUser().getId(), this.getRadio().getId(), this.getTrack().getId(), songFeedback, speedFeedback, dynamicsFeedback, moodFeedback);

        return plainFeedback;
    }


}

