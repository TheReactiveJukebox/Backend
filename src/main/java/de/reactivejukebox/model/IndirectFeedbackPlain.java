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

    public String getFeedbackName() {
        return feedbackName;
    }

    public void setFeedbackName(String name) {
        this.feedbackName = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }

    public int getToTrackId() {
        return toTrackId;
    }

    public void setToTrackId(int track) {
        this.toTrackId = track;
    }

    public boolean isValid() {
        if (radioId <= 0 || userId <= 0 || trackId <= 0 || position < 0) {
            return false;
        }
        try {
            IndirectFeedbackName feedback;
                feedback = IndirectFeedbackName.valueOf(feedbackName);
            switch (feedback) {
                case MULTI_SKIP:
                    if (toTrackId <= 0) {
                        return false;
                    }
                    break;
            }
        } catch (NullPointerException e) {
            System.err.println("Error: Invalid feedback, " + feedbackName + " not know as feedback art.");
            return false;
        } catch (Exception e) {
            System.err.println("Error: Invalid feedback");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
