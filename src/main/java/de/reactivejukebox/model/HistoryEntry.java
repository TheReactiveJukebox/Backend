package de.reactivejukebox.model;

import de.reactivejukebox.user.UserData;

public class HistoryEntry {

    private int trackId;
    private int radioId;
    private Track track;
    private Radio radio;
    private UserData user;

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

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public Radio getRadio() {
        return radio;
    }

    public void setRadio(Radio radio) {
        this.radio = radio;
    }

    @Override
    public String toString() {
        return "[user=" + user +
                ", track=" + track +
                ", radio=" + radio +
                "]";
    }
}
