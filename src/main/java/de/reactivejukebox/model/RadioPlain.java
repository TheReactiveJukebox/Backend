package de.reactivejukebox.model;


public class RadioPlain {
    private int id;
    private int userId;
    private boolean random;
    private String[] genres;
    private String mood;
    private int startYear;
    private int endYear;

    public RadioPlain(int id, int userId, boolean random, String[] genres, String mood, int startYear, int endYear) {
        this.id = id;
        this.userId = userId;
        this.random = random;
        this.genres = genres;
        this.mood = mood;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public RadioPlain(){

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

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
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
