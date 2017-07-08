package de.reactivejukebox.model;


import java.sql.Timestamp;

public class HistoryEntryD {
    private Integer id;
    private int trackId;
    private int radioId;
    private int userId;
    private Timestamp time;

    public HistoryEntryD(){}

    public HistoryEntryD(int id,int trackId, int radioId, int userId, Timestamp time){
        this.id = id;
        this.trackId =trackId;
        this.userId =userId;
        this.radioId=radioId;
        this.time = time;
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

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "[user=" + userId +
                ", track=" + trackId +
                ", radio=" + radioId +
                "]";
    }
}