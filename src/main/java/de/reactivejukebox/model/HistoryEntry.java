package de.reactivejukebox.model;


import java.sql.Timestamp;

public class HistoryEntry {

    private int id;
    private Track track;
    private Radio radio;
    private User user;
    private Timestamp time;

    public HistoryEntry() {
    }

    public HistoryEntry(int id, Track track, Radio radio, User user, Timestamp time) {
        this.id = id;
        this.track = track;
        this.radio = radio;
        this.user = user;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Radio getRadio() {
        return radio;
    }

    public void setRadio(Radio radio) {
        this.radio = radio;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "[user=" + user +
                ", track=" + track +
                ", radio=" + radio +
                "]";
    }

    public HistoryEntryPlain getPlainObject() {
        return new HistoryEntryPlain(id, track.getId(), radio.getId(), user.getId(), time);
    }
}
