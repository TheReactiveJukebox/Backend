package de.reactivejukebox.model;

public class IndirectFeedbackPlain {
    private int id; // Id in database for this entry
    private int radioId;
    private int userId;
    private int trackId; // current played song
    private int position; // Position in seconds in the Song
    private int toTrackId; // skip to this song
    private String feedbackName; // Name of the feedback

    public IndirectFeedbackPlain() {
    }

    public IndirectFeedbackPlain(int id, int radioId, int userId, int trackId, int position, int toTrackId, String feedbackName) {
        this.id = id;
        this.radioId = radioId;
        this.userId = userId;
        this.trackId = trackId;
        this.position = position;
        this.toTrackId = toTrackId;
        this.feedbackName = feedbackName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getRadioId() {
        return radioId;
    }

    public void setRadioId(int radioId) {
        this.radioId = radioId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    }

}
