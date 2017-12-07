package de.reactivejukebox.model;

import java.util.Date;

public class PlaylistPlain {
    private int[] tracks;
    private String title;
    private Date created;
    private Date edited;
    private int userId;
    private int id;
    private boolean isPublic;

    public PlaylistPlain() {
        // empty constructor for deserialization
    }

    public PlaylistPlain(int[] tracks, String title, Date created, Date edited, int userId, boolean isPublic) {
        this(0, tracks, title, created, edited, userId, isPublic);
    }

    public PlaylistPlain(int id, int[] tracks, String title, Date created, Date edited, int userId, boolean isPublic) {
        this.id = id;
        this.tracks = tracks;
        this.title = title;
        this.created = created;
        this.edited = edited;
        this.userId = userId;
        this.isPublic = isPublic;
    }

    public int[] getTracks() {
        return tracks;
    }

    public void setTracks(int[] tracks) {
        this.tracks = tracks;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getEdited() {
        return edited;
    }

    public void setEdited(Date edited) {
        this.edited = edited;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
