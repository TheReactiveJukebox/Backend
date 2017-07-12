package de.reactivejukebox.model;

import java.io.Serializable;

public class Radio implements Serializable {

    private int id;
    private User user;
    private boolean random;
    private String[] genres;
    private String mood;
    private int startYear;
    private int endYear;


    public Radio(int id, User user, boolean random, String[] genres, String mood, int startYear, int endYear) {
        this.id = id;
        this.user = user;
        this.random = random;
        this.genres = genres;
        this.mood = mood;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public Radio(){

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public RadioPlain getPlainObject(){
        return new RadioPlain(id, user.getId(),random,genres,mood,startYear,endYear);
    }

}
