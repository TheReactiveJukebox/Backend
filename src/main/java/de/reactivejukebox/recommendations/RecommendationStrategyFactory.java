package de.reactivejukebox.recommendations;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.strategies.RandomTracks;
import de.reactivejukebox.recommendations.strategies.SameArtistGreatestHits;
import de.reactivejukebox.recommendations.strategies.StrategyType;
import de.reactivejukebox.recommendations.traits.HistoryPredicate;

import java.util.Collection;
import java.util.function.Predicate;

public class RecommendationStrategyFactory {

    private Radio radio;
    private Collection<Track> upcoming;

    public RecommendationStrategyFactory(Radio radio, Collection<Track> upcoming) {
        this.radio = radio;
        this.upcoming = upcoming;
    }

    public RecommendationStrategy createStrategy(int resultCount) {
        return createStrategy(radio.getAlgorithm(), resultCount);
    }

    public RecommendationStrategy createStrategy(StrategyType s, int resultCount) {
        if (s == StrategyType.SAGH) {
            Predicate<Track> history = new HistoryPredicate(radio, upcoming);
            Collection<Track> base = radio.getStartTracks();
            return new SameArtistGreatestHits(history, base, resultCount);
        } else if (s == StrategyType.RANDOM) {
            Predicate<Track> history = new HistoryPredicate(radio, upcoming);
            return new RandomTracks(history,resultCount);
        } else throw new NoSuchStrategyException();
    }

    public RecommendationStrategy createStrategy(String strategyName, int resultCount) {
        return createStrategy(StrategyType.valueOf(strategyName), resultCount);
    }
}
