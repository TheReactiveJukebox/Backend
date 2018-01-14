package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.Recommendations;
import de.reactivejukebox.recommendations.filters.HistoryPredicate;
import de.reactivejukebox.recommendations.filters.MoodPredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoodNN implements RecommendationStrategy {

    private Collection<Track> selectedTracks;
    private int resultCount;
    private Radio radio;
    private Collection<Track> upcoming;
    private int windowMultiplicator;

    public MoodNN(Radio radio, Collection<Track> upcoming, int resultCount) {
        if(radio.getStartTracks() != null && radio.getStartTracks().size() > 0){
            this.selectedTracks = radio.getStartTracks();
        }
        else{
            this.selectedTracks = new ArrayList<>();
            if(radio.getValence()!=null && radio.getArousal()!=null && radio.getValence()!=0f && radio.getArousal()!=0f){
                this.selectedTracks.add(new Track(0,"",null,null,"","",0
                        ,0,null,0f,0f,"","",radio.getValence(),radio.getArousal()));
            }
        }
        this.radio = radio;
        this.upcoming = upcoming;
        this.resultCount = resultCount;
        this.windowMultiplicator = 1;
    }

    @Override
    public Recommendations getRecommendations() {
        List<Track> tracks;
        List<Float> score;

        do {
            tracks = defaultRecs();
            score = this.getScores(tracks);
            this.windowMultiplicator++;
        }while (tracks.size() < this.resultCount/4);


        Recommendations rec = new Recommendations(tracks, score);
        return rec;
    }

    private List<Track> defaultRecs() {
        return selectedTracks.stream().distinct().flatMap(this::nearestNeighbours).distinct() // Find near Mood
                .sorted(((o1, o2) -> Float.compare(calcDistance(o1), calcDistance(o2)))) // Sort with distance
                .limit(resultCount)
                .collect(Collectors.toList());
    }



    private List<Float> getScores(List<Track> recommendations) {
        return recommendations.stream().map(this::calcDistance).map(this::score).collect(Collectors.toList());
    }

    private float score(float distance) { // 1 when exact speed match, 0 when 5% away from selected mood, quadratic decay
        return (float) Math.pow((Math.max((MoodPredicate.startWindow * this.windowMultiplicator)- distance, 0)) / 0.1f, 2);
    }

    private Stream<Track> nearestNeighbours(Track t) {
        return   Model.getInstance().getTracks().stream()
                .filter(new MoodPredicate(t.getArousal(), t.getValence(), MoodPredicate.startWindow * this.windowMultiplicator)) // Filter track mood
                .filter(new HistoryPredicate(this.radio, this.upcoming)); // Filter history
    }

    private float calcDistance(Track t) {
            float result = Float.MAX_VALUE;
            for (Track e : selectedTracks) { // Get minimal Distance to the selected Tracks
                float dist = Math.abs(t.getArousal() - e.getArousal()) + Math.abs(t.getValence() - e.getValence());
                result = dist < result ? dist : result;
            }
            return result;
    }
}
