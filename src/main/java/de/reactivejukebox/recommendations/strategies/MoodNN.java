package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.filters.ArtistPredicate;
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
    Radio radio;
    Collection<Track> upcoming;

    public MoodNN(Radio radio, Collection<Track> upcoming, int resultCount){
        this.selectedTracks = radio.getStartTracks();
        this.radio = radio;
        this.upcoming = upcoming;
        this.resultCount = resultCount;
    }
    @Override
    public List<Track> getRecommendations() {
        return selectedTracks.stream().distinct().flatMap(this::nearestNeighbours).distinct()
                .sorted(Comparator.comparingDouble(Track::getRecScore))
                .limit(resultCount)
                .collect(Collectors.toList());
    }

    private Stream<Track> nearestNeighbours(Track t){
        Stream<Track> possibleTracks = radio.filter(Model.getInstance().getTracks().stream());
        float v = t.getValence();
        float a = t.getArousal();
        possibleTracks = possibleTracks.filter(new MoodPredicate(a,v))
                .map(track -> calcDistance(track,a,v));
        return  radio.filterHistory(possibleTracks,this.upcoming,this.resultCount);
    }

    private Track calcDistance(Track t, float a, float v){
        t.setRecScore(
                Math.abs(t.getArousal()-a)+Math.abs(t.getValence()-v)
        );
        return t;
    }
}
