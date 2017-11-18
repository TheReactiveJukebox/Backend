package de.reactivejukebox.model;

import de.reactivejukebox.recommendations.filters.GenrePredicate;
import de.reactivejukebox.recommendations.filters.HistoryPredicate;
import de.reactivejukebox.recommendations.filters.PublishedPredicate;
import de.reactivejukebox.recommendations.strategies.StrategyType;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Radio implements Serializable {

    private Integer id;
    private User user;
    private String[] genres;
    private String mood;
    private Integer startYear;
    private Integer endYear;
    private Float speed;
    private Float dynamic;
    private Float arousal;
    private Float valence;
    private List<Track> startTracks;
    private StrategyType algorithm;


    public Radio(
            Integer id,
            User user,
            String[] genres,
            String mood,
            Integer startYear,
            Integer endYear,
            Float speed,
            Float dynamic,
            Float arousal,
            Float valence,
            List<Track> startTracks,
            StrategyType algorithm) {
        this.id = id;
        this.user = user;
        this.genres = genres;
        this.mood = mood;
        this.startYear = startYear;
        this.speed = speed;
        this.dynamic = dynamic;
        this.endYear = endYear;
        this.startTracks = startTracks;
        this.algorithm = algorithm;
        this.arousal = arousal;
        this.valence = valence;
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

    public void setSpeed(Float speed) {
        this.speed = speed;
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

    public Stream<Track> filter(Stream<Track> trackStream) {
        if (getGenres() != null && getGenres().length > 0) {
            trackStream = trackStream.filter(new GenrePredicate(this));
        }
        if (getStartYear() != null || getEndYear() != null) {
            trackStream = trackStream.filter(new PublishedPredicate(this));
        }
        return trackStream;
    }

    public Stream<Track> filterHistory(Stream<Track> trackStream, Collection<Track> upcoming, int resultCount) {
        List<Track> allTracks = trackStream.collect(Collectors.toList());
        Set<Track> trackSet = allTracks.stream().filter(new HistoryPredicate(this, upcoming)).collect(Collectors.toSet());  // filter History
        if (trackSet.size() >= resultCount) {
            return trackSet.stream();       //result filtered for History
        } else {
            return allTracks.stream();             //result with already used tracks
        }
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
        return new RadioPlain(id, user.getId(), genres, mood, startYear, endYear, algorithmName, ids, speed, dynamic, arousal, valence);
    }
}
