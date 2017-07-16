package de.reactivejukebox.model;

import de.reactivejukebox.recommendations.strategies.StrategyType;

import java.io.Serializable;
import java.util.List;

public class Radio implements Serializable {

    private int id;
    private User user;
    private String[] genres;
    private String mood;
    private int startYear;
    private int endYear;
    private List<Track> startTracks;
    private StrategyType algorithm;


    public Radio(
            int id,
            User user,
            String[] genres,
            String mood,
            int startYear,
            int endYear,
            List<Track> startTracks,
            StrategyType algorithm) {
        this.id = id;
        this.user = user;
        this.genres = genres;
        this.mood = mood;
        this.startYear = startYear;
        this.endYear = endYear;
        this.startTracks = startTracks;
        this.algorithm = algorithm;
    }

    public Radio() {

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

    public List<Track> getStartTracks() {
        return startTracks;
    }

    public void setStartTracks(List<Track> startTracks) {
        this.startTracks = startTracks;
    }

    public StrategyType getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(StrategyType algorithm) {
        this.algorithm = algorithm;
    }

    public RadioPlain getPlainObject() {
        int[] ids = null;
        if (startTracks != null) {
            ids = new int[startTracks.size()];
            for (int i = 0; i < startTracks.size(); i++) {
                ids[i] = startTracks.get(i).getId();
            }
        }
        String algorithmName = algorithm != null ? algorithm.name() : null; // workaround for misuse of plain object
        return new RadioPlain(id, user.getId(), genres, mood, startYear, endYear, algorithmName, ids);
    }
}
