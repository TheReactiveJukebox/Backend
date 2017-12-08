package de.reactivejukebox.recommendations;

import de.reactivejukebox.model.Track;
import java.util.List;

public class Recommendations {

    private final List<Float> scores;
    private final List<Track> tracks;

    public Recommendations(List<Track> tracks, List<Float> scores) {
        this.tracks = tracks;
        this.scores = scores;
    }


    public List<Track> getTracks() {
        return tracks;
    }


    public List<Float> getScores() {
        return scores;
    }


}
