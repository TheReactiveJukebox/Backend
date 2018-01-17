package de.reactivejukebox.model;

import java.util.List;

/**
 * TrackFeedback is a model for a track feedback containing only ids for user and radio
 */
public class TrackFeedback {

    private int id; //global feedback id
    private int trackId;
    private int songFeedback;



    public TrackFeedback() {
    }

    public TrackFeedback(int id, int trackId, int songFeedback) {
        this.id = id;
        this.trackId = trackId;
        this.songFeedback = songFeedback;
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

}
