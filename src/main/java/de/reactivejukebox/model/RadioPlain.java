package de.reactivejukebox.model;

public class RadioPlain {
    private int id;
    private int userId;
    private String[] genres;
    private String mood;
    private int startYear;
    private int endYear;
    private String algorithm;
    float speed;
    float dynamic;
    private int[] startTracks;

    public RadioPlain(
            int id,
            int userId,
            String[] genres,
            String mood,
            int startYear,
            int endYear,
            String algorithm,
            int[] startTracks,
            float speed,
            float dynamic) {
        this.id = id;
        this.userId = userId;
        this.genres = genres;
        this.mood = mood;
        this.startYear = startYear;
        this.endYear = endYear;
        this.algorithm = algorithm;
        this.startTracks = startTracks;
        this.speed = speed;
        this.dynamic = dynamic;
    }

    public RadioPlain() {

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

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int[] getStartTracks() {
        return startTracks;
    }

    public void setStartTracks(int[] startTracks) {
        this.startTracks = startTracks;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDynamic() {
        return dynamic;
    }

    public void setDynamic(float dynamic) {
        this.dynamic = dynamic;
    }
}
