package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.UserProfile;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.RecommendationStrategyFactory;
import de.reactivejukebox.recommendations.Recommendations;

import java.util.*;
import java.util.function.Predicate;

public class HybridStrategy implements RecommendationStrategy {
    private static float likeMod = 1.5f;
    private static float disLikeMod = -0.5f;
    private static float artistMod = 1.25f;
    private static float disArtistMod = -0.75f;
    private static float albumMod = 1.25f;
    private static float disAlbumMod = -0.75f;

    private UserProfile userProfile;
    private RecommendationStrategyFactory factory;
    List<Predicate<Track>> radioPredicates;

    public HybridStrategy(RecommendationStrategyFactory factory, Radio radio, UserProfile userProfile) {
        this.factory = factory;
        this.userProfile = userProfile;
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

    private Recommendations modifyRanking (Map<Track,Float> input){
        ArrayList<Track> tracks = new ArrayList<>();
        ArrayList<Float> scores = new ArrayList<>();
        ArrayList<Float> mods = new ArrayList<>();

        tracks.addAll(input.keySet());
        for (Track t: tracks) {
            Integer tId = t.getId();

            Float tMod = likeMod * userProfile.getTrackFeedback(tId) + disLikeMod * userProfile.getTrackFeedback(tId);
            if (tMod > 0) mods.add(tMod);
            Float arMod = artistMod * userProfile.getArtistFeedback(tId) + disArtistMod * userProfile.getArtistFeedback(tId);
            if (arMod > 0) mods.add(arMod);
            Float alMod = albumMod * userProfile.getAlbumFeedback(tId) + disAlbumMod * userProfile.getAlbumFeedback(tId);
            if (alMod > 0) mods.add(alMod);

            Float value = input.get(t);
            for (Float f : mods){
                value *= f;
            }
            scores.add(value);
        }
        return new Recommendations(tracks, scores);
    }
}
