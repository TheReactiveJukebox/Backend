package de.reactivejukebox.model;


public class HistoryEntry {

    private int trackId;
    private int radioId;
    private int userId;

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

    @Override
    public String toString() {
        return "[user=" + userId +
                ", track=" + trackId +
                ", radio=" + radioId +
                "]";
    }
}
