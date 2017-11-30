package de.reactivejukebox.model;

public class GenreFeedback {
    private String genre;
    private int feedback;

    public GenreFeedback(){}

    public GenreFeedback(String genre, int feedback){
        this.genre = genre;
        this.feedback = feedback;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }
}
