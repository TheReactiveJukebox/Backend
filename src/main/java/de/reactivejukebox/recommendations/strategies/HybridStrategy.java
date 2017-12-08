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
    private Map<StrategyType, Float> weights;

    public HybridStrategy(RecommendationStrategyFactory factory, Radio radio) {
        this.factory = factory;
        this.radioPredicates = radio.getPredicates();
    }

    @Override
    public Recommendations getRecommendations() {
        Map<Track, Float> results = new HashMap<>();
        for (StrategyType strategyType : StrategyType.values()) {
            RecommendationStrategy strat = factory.createStrategy(strategyType, 0); // TODO decide where to set resultCount

            Recommendations algorithmResults = strat.getRecommendations(); // TODO

            Iterator<Track> trackIterator = algorithmResults.getTracks().iterator();
            Iterator<Float> scoreIterator = algorithmResults.getScores().iterator();

            outerLoop:
            while (trackIterator.hasNext() && scoreIterator.hasNext()) {
                Track track = trackIterator.next();
                float score = scoreIterator.next();
                for (Predicate<Track> p : radioPredicates) {
                    if (!p.test(track)) {
                        continue outerLoop;
                    }
                }

                score *= weights.get(strategyType);
                if (results.containsKey(track)) {
                    results.put(track, results.get(track) + score);
                } else {
                    results.put(track, score);
                }
            }
        }
        ArrayList<Track> recommendations = new ArrayList<>();
        recommendations.addAll(results.keySet());
        recommendations.sort((trackL, trackR) -> Float.compare(results.get(trackL), results.get(trackR)));
        return new Recommendations(recommendations, null);
    }
}
