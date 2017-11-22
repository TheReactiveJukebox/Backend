package de.reactivejukebox.model;

public class AlbumFeedback {
    private int album;
    private int feedback;

    public AlbumFeedback(){}

    public AlbumFeedback(int album, int feedback){
        this.album = album;
        this.feedback = feedback;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int artist) {
        this.album = album;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }
}