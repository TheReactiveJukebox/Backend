package de.reactivejukebox.model;

/**
 * TrackFeedbackPlain is a model for a track feedback containing only ids for user and radio
 */
public class TrackFeedbackPlain {

    private int id; //global feedback id
    private int userId;
    private int radioId;
    private int trackId;
    private int songFeedback;
    private int artistFeedback;
    private int speedFeedback;
    private int genreFeedback;
    private int dynamicsFeedback;
    private int moodFeedback;


    public TrackFeedbackPlain() {
    }

    public TrackFeedbackPlain(int id, int userId, int radioId, int trackId, int songFeedback, int artistFeedback, int speedFeedback, int genreFeedback, int dynamicsFeedback, int moodFeedback) {
        this.id = id;
        this.userId = userId;
        this.radioId = radioId;
        this.trackId = trackId;
        this.songFeedback = songFeedback;
        this.artistFeedback = artistFeedback;
        this.speedFeedback = speedFeedback;
        this.genreFeedback = genreFeedback;
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

    public int getRadioId() {
        return radioId;
    }

    public void setRadioId(int radioId) {
        this.radioId = radioId;
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


    public int getArtistFeedback() {
        return artistFeedback;
    }

    public void setArtistFeedback(int artistFeedback) {
        this.artistFeedback = artistFeedback;
    }


    public int getSpeedFeedback() {
        return speedFeedback;
    }

    public void setSpeedFeedback(int speedFeedback) {
        this.speedFeedback = speedFeedback;
    }


    public int getGenreFeedback() {
        return genreFeedback;
    }

    public void setGenreFeedback(int genreFeedback) {
        this.genreFeedback = genreFeedback;
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
