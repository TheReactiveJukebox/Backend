package de.reactivejukebox.model;

import de.reactivejukebox.recommendations.filters.GenrePredicate;
import de.reactivejukebox.recommendations.filters.HistoryPredicate;
import de.reactivejukebox.recommendations.filters.MoodPredicate;
import de.reactivejukebox.recommendations.filters.PublishedPredicate;
import de.reactivejukebox.recommendations.filters.SpeedPredicate;
import de.reactivejukebox.recommendations.strategies.StrategyType;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Radio implements Serializable {

    private Integer id;
    private User user;
    private String[] genres;
    private Integer startYear;
    private Integer endYear;
    private Float dynamic;
    private Float arousal;
    private Float valence;
    private Float minSpeed;
    private Float maxSpeed;
    private List<Track> startTracks;
    private StrategyType algorithm;


    public Radio(
            Integer id,
            User user,
            String[] genres,
            Integer startYear,
            Integer endYear,
            Float dynamic,
            Float arousal,
            Float valence,
            Float minSpeed,
            Float maxSpeed,
            List<Track> startTracks,
            StrategyType algorithm) {
        this.id = id;
        this.user = user;
        this.genres = genres;
        this.startYear = startYear;
        this.dynamic = dynamic;
        this.endYear = endYear;
        this.startTracks = startTracks;
        this.algorithm = algorithm;
        this.arousal = arousal;
        this.valence = valence;
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
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

    public Float getDynamic() {
        return dynamic;
    }

    public Float getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(Float minSpeed) {
        this.minSpeed = minSpeed;
    }

    public Float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Stream<Track> filter(Stream<Track> trackStream) {
        for (Predicate<Track> p : getPredicates()) {
            trackStream = trackStream.filter(p);
        }
        return trackStream;
    }

    public List<Predicate<Track>> getPredicates() {
        ArrayList<Predicate<Track>> predicates = new ArrayList<>();
        if (getGenres() != null && getGenres().length > 0) {
            predicates.add(new GenrePredicate(this));
        }
        if (getStartYear() != null || getEndYear() != null) {
            predicates.add(new PublishedPredicate(this));
        }
        if (getMinSpeed()!= null || getMaxSpeed()!= null ){
            predicates.add(new SpeedPredicate(this));
        }
        if (getArousal() != null || getValence() != null) {
            predicates.add(new MoodPredicate(this));
        }
        return predicates;
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
        return new RadioPlain(id, user.getId(), genres, startYear, endYear, algorithmName, ids, dynamic, arousal, valence, minSpeed, maxSpeed);
    }
}
