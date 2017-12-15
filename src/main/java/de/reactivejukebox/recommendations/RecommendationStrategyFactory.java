package de.reactivejukebox.recommendations;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.UserProfile;
import de.reactivejukebox.recommendations.strategies.*;

import java.sql.SQLException;
import java.util.Collection;


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
            return new SameArtistGreatestHits(radio, upcoming, resultCount);
        } else if (s == StrategyType.RANDOM) {
            return new RandomTracks(radio, upcoming, resultCount);
        } else if (s == StrategyType.HYBRID) {
            try {
                return new HybridStrategy(this, radio.getPredicates(), new UserProfile(radio));
            } catch (SQLException e) {
                if (radio.getUser() != null) {
                    System.err.println("Could not obtain user profile for user "
                            + radio.getUser().getId()
                            + ". Exception:");
                } else {
                    System.err.println("Could not obtain user from radio. Exception: ");
                }
                e.printStackTrace();
                return new HybridStrategy(this, radio.getPredicates(), null);
            }
        } else if (s == StrategyType.MOOD) {
            return new MoodNN(radio, upcoming, resultCount);
        } else throw new NoSuchStrategyException();
    }

    public RecommendationStrategy createStrategy(String strategyName, int resultCount) {
        return createStrategy(StrategyType.valueOf(strategyName), resultCount);
    }
}
