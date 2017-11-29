package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.filters.MoodPredicate;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by David on 28.11.2017.
 */
public class MoodNN implements RecommendationStrategy{

    private Collection<Track> selectedTracks;
    int resultCount;

    public MoodNN(Radio radio, Collection<Track> upcoming, int resultCount){
        this.selectedTracks = radio.getStartTracks();
        this.resultCount = resultCount;
    }
    @Override
    public List<Track> getRecommendations() {
        return selectedTracks.stream().distinct().flatMap(this::nearestNeighbours)
                .sorted(Comparator.comparingDouble(Track::getRecScore))
                .limit(resultCount)
                .collect(Collectors.toList());

    }

    private Stream<Track> nearestNeighbours(Track t){
        float v = t.getValence();
        float a = t.getArousal();
        return Model.getInstance().getTracks().stream().filter(new MoodPredicate(a,v))
                .map(track -> calcDistance(track,a,v));
    }

    private Track calcDistance(Track t, float a, float v){
        t.setRecScore(
                Math.abs(t.getArousal()-a)+Math.abs(t.getValence()-v)
        );
        return t;
    }

    private class DistanceComperator implements Comparator<Track>{

        @Override
        public int compare(Track o1, Track o2) {
            return 0;
        }
    }
}
