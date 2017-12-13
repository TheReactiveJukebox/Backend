package de.reactivejukebox.recommendations;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.UserProfile;
import de.reactivejukebox.recommendations.strategies.HybridStrategy;
import de.reactivejukebox.recommendations.strategies.MoodNN;
import de.reactivejukebox.recommendations.strategies.RandomTracks;
import de.reactivejukebox.recommendations.strategies.SameArtistGreatestHits;
import de.reactivejukebox.recommendations.strategies.StrategyType;

import java.sql.SQLException;
import java.util.Collection;


public class RecommendationStrategyFactory {

    private Radio radio;
    private UserProfile userProfile;
    private Collection<Track> upcoming;

    public RecommendationStrategyFactory(Radio radio, Collection<Track> upcoming) throws SQLException {
        this.radio = radio;
        this.upcoming = upcoming;
        this.userProfile = new UserProfile(radio);
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
            return new HybridStrategy(this, radio, userProfile);
        } else if (s == StrategyType.MOOD){
            return new MoodNN(radio, upcoming, resultCount);
        } else throw new NoSuchStrategyException();
    }

    public RecommendationStrategy createStrategy(String strategyName, int resultCount) {
        return createStrategy(StrategyType.valueOf(strategyName), resultCount);
    }
}
