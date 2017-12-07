package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.filters.ArtistPredicate;
import de.reactivejukebox.recommendations.filters.HistoryPredicate;
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
    private int resultCount;
    private Radio radio;
    private Collection<Track> upcoming;

    public MoodNN(Radio radio, Collection<Track> upcoming, int resultCount){
        this.selectedTracks = radio.getStartTracks();
        this.radio = radio;
        this.upcoming = upcoming;
        this.resultCount = resultCount;
    }

    @Override
    public List<Track> getRecommendations() {
        return selectedTracks.stream().distinct().flatMap(this::nearestNeighbours).distinct()//Find near Mood
                .sorted(((o1, o2) -> Float.compare(calcDistance(o1),calcDistance(o2))))//Sort with distance
                .limit(resultCount)
                .collect(Collectors.toList());
    }

    private Stream<Track> nearestNeighbours(Track t){
        return radio.filter(Model.getInstance().getTracks().stream())//Filter Radio presets
                .filter(new MoodPredicate(t.getArousal(),t.getValence()))//Filter track mood
                .filter(new HistoryPredicate(this.radio,this.upcoming));//Filter history
    }

    private float calcDistance(Track t){
        float result = Float.MAX_VALUE;
        for (Track e:selectedTracks){//Get minimal Distance to the selected Tracks
            float dist = Math.abs(t.getArousal()-e.getArousal())+Math.abs(t.getValence()-e.getValence());
            result = dist < result ? dist : result;
        }
        return result;
    }
}
