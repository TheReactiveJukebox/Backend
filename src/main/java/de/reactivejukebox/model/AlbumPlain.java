package de.reactivejukebox.model;

public class AlbumPlain implements MusicEntityPlain {

    protected String title;
    protected int artist;
    protected int id;
    protected AlbumFeedback feedback;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getArtist() {
        return artist;
    }

    public void setArtist(int artist) {
        this.artist = artist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AlbumFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(AlbumFeedback feedback) {
        this.feedback = feedback;
    }
}
