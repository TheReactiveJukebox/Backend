package de.reactivejukebox.model;

import de.reactivejukebox.recommendations.filters.GenrePredicate;
import de.reactivejukebox.recommendations.filters.PublishedPredicate;
import de.reactivejukebox.recommendations.strategies.StrategyType;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

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
        startTracks = new LinkedList<>();
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

    public Stream<Track> filter (Stream<Track> trackStream){
        if (getGenres() != null && getGenres().length > 0) {
            trackStream = trackStream.filter(new GenrePredicate(this));
        }
        if (getStartYear() > 0 && getEndYear() > 0) {
            trackStream = trackStream.filter(new PublishedPredicate(this));
        }
        return trackStream;
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
