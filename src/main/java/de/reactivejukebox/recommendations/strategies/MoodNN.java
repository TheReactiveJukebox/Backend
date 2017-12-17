package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.Recommendations;
import de.reactivejukebox.recommendations.filters.HistoryPredicate;
import de.reactivejukebox.recommendations.filters.MoodPredicate;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoodNN implements RecommendationStrategy {

    private Collection<Track> selectedTracks;
    private int resultCount;
    private Radio radio;
    private Collection<Track> upcoming;
    private float arousal, valence, window;

    public MoodNN(Radio radio, Collection<Track> upcoming, int resultCount) {
        this(radio, upcoming, resultCount, 0f, 0f, 0f);
    }

    public MoodNN(Radio radio, Collection<Track> upcoming, int resultCount, float arousal, float valence, float window) {
        this.selectedTracks = radio.getStartTracks();
        this.radio = radio;
        this.upcoming = upcoming;
        this.resultCount = resultCount;
        this.arousal = arousal;
        this.valence = valence;
        this.window = window;
    }

    @Override
    public Recommendations getRecommendations() {
        List<Track> tracks;
        if (this.arousal == 0f || this.valence == 0f || this.window == 0f)
            tracks = defaultRecs();
        else
            tracks = hybridRecs();
        List<Float> score = this.getScores(tracks);
        Recommendations rec = new Recommendations(tracks, score);
        return rec;

    }

    private List<Track> defaultRecs() {
        return selectedTracks.stream().distinct().flatMap(this::nearestNeighbours).distinct() // Find near Mood
                .sorted(((o1, o2) -> Float.compare(calcDistance(o1), calcDistance(o2)))) // Sort with distance
                .limit(resultCount)
                .collect(Collectors.toList());
    }

    private List<Track> hybridRecs() {
        return radio.filter(Model.getInstance().getTracks().stream())
                .filter(new MoodPredicate(this.arousal, this.valence, this.window))
                .filter(new HistoryPredicate(this.radio, this.upcoming))
                .sorted(((o1, o2) -> Float.compare(calcDistance(o1), calcDistance(o2))))
                .limit(this.resultCount)
                .collect(Collectors.toList());
    }

    private List<Float> getScores(List<Track> recommendations) {
        return recommendations.stream().map(this::calcDistance).map(this::score).collect(Collectors.toList());
    }

    private float score(float distance) { // 1 when exact speed match, 0 when 5% away from selected mood, quadratic decay
        return (float) Math.pow((Math.max(0.1f - distance, 0)) / 0.1f, 2);
    }

    private Stream<Track> nearestNeighbours(Track t) {
        return radio.filter(Model.getInstance().getTracks().stream()) // Filter Radio presets
                .filter(new MoodPredicate(t.getArousal(), t.getValence())) // Filter track mood
                .filter(new HistoryPredicate(this.radio, this.upcoming)); // Filter history
    }

    private float calcDistance(Track t) {
        if (this.arousal != 0f && this.valence != 0f)
            return Math.abs(t.getArousal() - this.arousal) + Math.abs(t.getValence() - this.valence);
        else {
            float result = Float.MAX_VALUE;
            for (Track e : selectedTracks) { // Get minimal Distance to the selected Tracks
                float dist = Math.abs(t.getArousal() - e.getArousal()) + Math.abs(t.getValence() - e.getValence());
                result = dist < result ? dist : result;
            }
            return result;
        }

    }
}
