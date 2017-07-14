package de.reactivejukebox.recommendations;

import de.reactivejukebox.recommendations.strategies.SameArtistGreatestHits;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

public class RecommendationStrategyFactory {

    public RecommendationStrategyFactory() { // TODO add all basic parameters, e.g. User

    }

    public RecommendationStrategy createStrategy(String strategyName) { // TODO could also add them here
        if (strategyName == null) {
            throw new NotImplementedException();
            // TODO return default strategy for user
        } else if (strategyName.equals("sagh")) {
            // TODO factory knows how to obtain needed0 parameters. Add algorithm parameters in constructor and provide them here
            return new SameArtistGreatestHits(new ArrayList<>()); // TODO
        } else throw new NoSuchStrategyException();
    }
}
