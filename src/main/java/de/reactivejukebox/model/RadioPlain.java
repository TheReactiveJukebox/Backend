package de.reactivejukebox.model;

public class RadioPlain {
    private Integer id;
    private Integer userId;
    private String[] genres;
    private String mood;
    private Integer startYear;
    private Integer endYear;
    private String algorithm;
    private Float speed;
    private Float dynamic;
    private Float arousal;
    private Float valence;
    private int[] startTracks;

    public RadioPlain(
            Integer id,
            Integer userId,
            String[] genres,
            String mood,
            Integer startYear,
            Integer endYear,
            String algorithm,
            int[] startTracks,
            Float speed,
            Float dynamic,
            Float arousal,
            Float valence) {
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
        this.arousal = arousal;
        this.valence = valence;
    }

    public RadioPlain() {

    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
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

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getDynamic() {
        return dynamic;
    }

    public void setDynamic(Float dynamic) {
        this.dynamic = dynamic;
    }

    public Float getArousal() {
        return arousal;
    }

    public void setArousal(Float arousal) {
        this.arousal = arousal;
    }

    public Float getValence() {
        return valence;
    }

    public void setValence(Float valence) {
        this.valence = valence;
    }
}
