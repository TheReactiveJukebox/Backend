package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.RecommendationStrategyFactory;
import de.reactivejukebox.recommendations.Recommendations;

import java.util.*;
import java.util.function.Predicate;

public class HybridStrategy implements RecommendationStrategy {

    private RecommendationStrategyFactory factory;
    List<Predicate<Track>> radioPredicates;

    public HybridStrategy(RecommendationStrategyFactory factory, Radio radio) {
        this.factory = factory;
        this.radioPredicates = radio.getPredicates();
    }

    @Override
    public Recommendations getRecommendations() {
        Map<Track, Float> results = new HashMap<>();

        for (StrategyType strategy : StrategyType.values()) {
            // if algorihtm's weight is essentially 0, don't execute it
            float weight = strategy.getWeight();
            if (Math.abs(0 - weight) < 0.001) {
                continue;
            }

            // otherwise, instantiate and call algorithm
            RecommendationStrategy algorithm = factory.createStrategy(strategy, 0); // TODO decide where to set resultCount
            Recommendations algorithmResults = algorithm.getRecommendations();

            // iterate over tracks and scores simultaneously
            Iterator<Track> trackIterator = algorithmResults.getTracks().iterator();
            Iterator<Float> scoreIterator = algorithmResults.getScores().iterator();

            recommendedTrackLoop:
            while (trackIterator.hasNext() && scoreIterator.hasNext()) {
                Track track = trackIterator.next();
                float score = scoreIterator.next();

                // if song does not fit filter criteria, leave it out
                for (Predicate<Track> p : radioPredicates) {
                    if (!p.test(track)) {
                        continue recommendedTrackLoop;
                    }
                }

                // otherwise, compute final track score considering algorithm weight and add track to results
                score *= strategy.getWeight();
                if (results.containsKey(track)) {
                    results.put(track, results.get(track) + score);
                } else {
                    results.put(track, score);
                }
            }
        }
        // finally, collect tracks and sort them by score
        ArrayList<Track> recommendations = new ArrayList<>();
        recommendations.addAll(results.keySet());
        recommendations.sort((trackL, trackR) -> Float.compare(results.get(trackL), results.get(trackR)));

        /* If need be, we could also assemble a list of scores like this:

        ArrayList<Float> scores = new ArrayList<>();
        for (Track t : recommendations) {
            scores.add(results.get(t));
        }

        * and then...

        return new Recommendations(recommendations, scores);

        * But that's not necessary at the moment, so we save those CPU cycles.
        * If you ever end up here debugging a NullPointerException when accessing
        * Recommendations.getScores(), you know what to do.
        */

        return new Recommendations(recommendations, null);
    }
}
