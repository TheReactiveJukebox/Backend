package de.reactivejukebox.recommendations;

import de.reactivejukebox.model.Track;
import java.util.ArrayList;

public class Recommendations {

    private final ArrayList<Float> scores;
    private final ArrayList<Track> tracks;

    public Recommendations(ArrayList<Track> tracks, ArrayList<Float> scores) {
        this.tracks = tracks;
        this.scores = scores;
    }


    public ArrayList<Track> getTracks() {
        return tracks;
    }


    public ArrayList<Float> getScores() {
        return scores;
    }


}
