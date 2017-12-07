package de.reactivejukebox.model;

public class ArtistFeedback {
    private int artist;
    private int feedback;

    public ArtistFeedback(){}

    public ArtistFeedback(int artist, int feedback){
        this.artist = artist;
        this.feedback = feedback;
    }

    public int getArtist() {
        return artist;
    }

    public void setArtist(int artist) {
        this.artist = artist;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }
}
