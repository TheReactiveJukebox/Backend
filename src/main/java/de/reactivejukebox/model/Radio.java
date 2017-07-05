package de.reactivejukebox.model;

import java.io.Serializable;

public class Radio implements Serializable {

    private int id;
    private boolean isRandom;
    private String[] genres;
    private String mood;
    private int startYear;
    private int endYear;


    public Radio(int id, String feedbacklink, String[] genres, String mood, int startYear, int endYear ,  boolean isRandom) {
        this.id = id;
        this.isRandom = isRandom;
    }

    public Radio(){

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        this.isRandom = random;
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
