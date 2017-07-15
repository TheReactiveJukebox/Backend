package de.reactivejukebox.recommendations;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.strategies.SameArtistGreatestHits;
import de.reactivejukebox.recommendations.strategies.StrategyType;
import de.reactivejukebox.recommendations.strategies.traits.HistoryAwareness;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecommendationStrategyFactory {

    private Radio radio;

    public RecommendationStrategyFactory(Radio radio) {
        this.radio = radio;
    }

    public RecommendationStrategy createStrategy(int count) {
        return createStrategy(radio.getAlgorithm(), count);
    }

    public RecommendationStrategy createStrategy(StrategyType s, int resultCount) {
        if (s == StrategyType.SAGH) {
            Collection<Track> history = HistoryAwareness.recentHistory(radio.getUser());
            Collection<Track> base = radio.getStartTracks();
            return new SameArtistGreatestHits(history, base, resultCount);
        } else if (s == StrategyType.RANDOM) {
            // TODO return "Random" Recommendation Algorithm
            throw new NotImplementedException();
        } else throw new NoSuchStrategyException();
    }

    public RecommendationStrategy createStrategy(String strategyName, int resultCount) {
        return createStrategy(StrategyType.valueOf(strategyName), resultCount);
    }
}
