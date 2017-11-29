package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by David on 28.11.2017.
 */
public class MoodNN implements RecommendationStrategy{

    private Collection<Track> selectedTracks;

    public MoodNN(Radio radio, Collection<Track> upcoming, int resultCount){
        this.selectedTracks = radio.getStartTracks();
    }
    @Override
    public List<Track> getRecommendations() {
        return null;
    }

    private class DistanceComperator implements Comparator<Track>{

        @Override
        public int compare(Track o1, Track o2) {
            return 0;
        }
    }
}
