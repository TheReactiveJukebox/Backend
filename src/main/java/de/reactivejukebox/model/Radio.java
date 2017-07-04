package de.reactivejukebox.model;

public class Radio {

    private int id;
    private boolean random;
    private String referenceSongId;

    private String feedbacklink;
    private String[] genres;
    private String[] mood;
    private int startYear;
    private int endYear;


    public Radio(int id, boolean random, String referenceSongId, String feedbacklink, String[] genres, String[] mood, int startYear, int endYear ) {
        this.id = id;
        this.random = random;
        this.referenceSongId = referenceSongId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public String getReferenceSongId() {
        return referenceSongId;
    }

    public void setReferenceSongId(String refereceSongId) {
        this.referenceSongId = referenceSongId;
    }

    public String getFeedbacklink() {
        return feedbacklink;
    }

    public void setFeedbacklink(String feedbacklink) {
        this.feedbacklink = feedbacklink;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getMood() {
        return mood;
    }

    public void setMood(String[] mood) {
        this.mood = mood;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }
}
