package de.reactivejukebox.model;

import java.io.Serializable;

/**
 * The TrackFeedback class is a model for a single track feedback
 */
public class TrackFeedback implements Serializable {

    private int id; //global feedback id
    private User user;
    private Radio radio;
    private Track track;

    private int songFeedback;
    private int artistFeedback;
    private int speedFeedback;
    private int genreFeedback;
    private int dynamicsFeedback;
    private int periodFeedback;
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

    public Track getTrack() { return track; }

    public void setTrack(Track track) { this.track = track; }

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


    public int getPeriodFeedback() {
        return periodFeedback;
    }

    public void setPeriodFeedback(int periodFeedback) {
        this.periodFeedback = periodFeedback;
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
        TrackFeedbackPlain plainFeedback = new TrackFeedbackPlain();
        plainFeedback.setUserId(this.getUser().getId());
        plainFeedback.setRadioId(this.getRadio().getId());
        plainFeedback.setId(this.getId());
        plainFeedback.setTrackId(this.getTrack().getId());

        plainFeedback.setSongDisliked(this.songFeedback < 0);
        plainFeedback.setSongLiked(this.songFeedback > 0);

        plainFeedback.setArtistDisliked(this.artistFeedback < 0);
        plainFeedback.setArtistLiked(this.artistFeedback > 0);

        plainFeedback.setSpeedDisliked(this.speedFeedback < 0);
        plainFeedback.setSpeedLiked(this.speedFeedback > 0);

        plainFeedback.setGenreDisliked(this.genreFeedback < 0);
        plainFeedback.setGenreLiked(this.genreFeedback > 0);

        plainFeedback.setPeriodDisliked(this.periodFeedback < 0);
        plainFeedback.setPeriodLiked(this.periodFeedback > 0);

        plainFeedback.setMoodDisliked(this.moodFeedback < 0);
        plainFeedback.setMoodLiked(this.moodFeedback > 0);

        plainFeedback.setDynamicsDisliked(this.dynamicsFeedback < 0);
        plainFeedback.setDynamicsLiked(this.dynamicsFeedback > 0);

        return plainFeedback;
    }


}

