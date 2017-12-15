package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.UserProfile;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.RecommendationStrategyFactory;
import de.reactivejukebox.recommendations.Recommendations;

import java.util.*;
import java.util.function.Predicate;

public class HybridStrategy implements RecommendationStrategy {

    private enum FeedbackModifier {
        LIKE_TRACK(1.5f),
        DISLIKE_TRACK(0.5f),
        LIKE_ARTIST(1.25f),
        DISLIKE_ARTIST(0.75f),
        LIKE_ALBUM(1.25f),
        DISLIKE_ALBUM(0.75f),
        LIKE_TEMPO(1.25f),
        DISLIKE_TEMPO(0.75f),
        LIKE_MOOD(1.25f),
        DISLIKE_MOOD(0.75f),
        LIKE_GENRE(1.25f),
        DISLIKE_GENRE(0.75f),
        SKIP(1.8f),
        DELETE(1f),
        MULTISKIP(0.7f);

        public float value;

        FeedbackModifier(float value) {
            this.value = value;
        }
    }

    private RecommendationStrategyFactory factory;
    private UserProfile userProfile;
    private boolean respectUserProfile = false;
    private List<Predicate<Track>> radioTrackFilters;

    public HybridStrategy(RecommendationStrategyFactory factory, List<Predicate<Track>> radioTrackFilters, UserProfile userProfile) {
        this.factory = factory;

        // predicates should be there, just in case
        if (radioTrackFilters != null) {
            this.radioTrackFilters = radioTrackFilters;
        } else {
            this.radioTrackFilters = Collections.emptyList();
        }

        // userProfile might be null during testing
        if (userProfile != null) {
            this.userProfile = userProfile;
            respectUserProfile = true;
        }
    }

    @Override
    public Recommendations getRecommendations() {
        Map<Track, Float> results = new HashMap<>();

        // get recommendations from all algorithms
        for (StrategyType strategy : StrategyType.values()) {
            // if algorihtm's weight is essentially 0, don't execute it
            if (Math.abs(0 - strategy.getWeight()) < 0.001) {
                continue;
            }

            // otherwise, instantiate and call algorithm, gather results
            gatherAlgorithmResults(results, strategy);
        }

        // modify Ranking
        if (respectUserProfile) {
            applyUserFeedback(results, userProfile);
            applyHistory(results, userProfile);
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


    /**
     * Instantiates and calls an algorithm to get its recommendations.
     *
     * @param results  the intermediate data structure used to keep track of scores in getRecommendations()
     * @param strategy the algorithm to execute
     */
    void gatherAlgorithmResults(Map<Track, Float> results, StrategyType strategy) {
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
            for (Predicate<Track> p : radioTrackFilters) {
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

    /**
     * Inverse linear function to map an amount of, say, skip actions to a score multiplier.
     * The more skip actions the user performed on the track, the smaller the value.
     * The importance of skip actions can be tuned by adjusting the corresponding modifier
     * instead of adjusting this function directly.
     * <p>
     * To be used for negative actions like skips, multiskips, deletes.
     *
     * @param modifier the action the user executed n times
     * @param n        how often the action was executed
     * @return the final modifier, considering how often the action was executed on that specific feature
     */
    float calculateLinearModifier(FeedbackModifier modifier, int n) {
        return 1 / (modifier.value * n + 1);
    }

    /**
     * Applies all user feedback to modify the ranking
     *
     * @param ranking the intermediate data structure used to keep track of scores in getRecommendations()
     * @param profile the profile containing all of the user's feedback
     */
    void applyUserFeedback(Map<Track, Float> ranking, UserProfile profile) {
        for (Map.Entry<Track, Float> entry : ranking.entrySet()) {
            Track t = entry.getKey();
            int trackId = t.getId();
            float score = entry.getValue();

            // apply direct feedback modifiers: track, artist, album, tempo, mood
            if (profile.getTrackFeedback(trackId) == 1) {
                score *= FeedbackModifier.LIKE_TRACK.value;
            } else if (profile.getTrackFeedback(trackId) == -1) {
                score *= FeedbackModifier.DISLIKE_TRACK.value;
            }

            if (profile.getArtistFeedback(trackId) == 1) {
                score *= FeedbackModifier.LIKE_ARTIST.value;
            } else if (profile.getTrackFeedback(trackId) == -1) {
                score *= FeedbackModifier.DISLIKE_ARTIST.value;
            }

            if (profile.getAlbumFeedback(trackId) == 1) {
                score *= FeedbackModifier.LIKE_ALBUM.value;
            } else if (profile.getAlbumFeedback(trackId) == -1) {
                score *= FeedbackModifier.DISLIKE_ALBUM.value;
            }

            if (profile.getSpeedFeedback(entry.getKey().getSpeed()) == 1) {
                score *= FeedbackModifier.LIKE_TEMPO.value;
            } else if (profile.getSpeedFeedback(entry.getKey().getSpeed()) == -1) {
                score *= FeedbackModifier.DISLIKE_TEMPO.value;
            }

            if (profile.getMoodFeedback(t.getArousal(), t.getValence()) == 1) {
                score *= FeedbackModifier.LIKE_MOOD.value;
            } else if (profile.getMoodFeedback(t.getArousal(), t.getValence()) == -1) {
                score *= FeedbackModifier.DISLIKE_MOOD.value;
            }

            // TODO incorporate genre feedback

            // apply indirect feedback modifiers: how often was the track skipped, deleted, skipped over
            score *= calculateLinearModifier(FeedbackModifier.SKIP, profile.getSkipFeedback(trackId));
            score *= calculateLinearModifier(FeedbackModifier.DELETE, profile.getDeleteFeedback(trackId));
            score *= calculateLinearModifier(FeedbackModifier.MULTISKIP, profile.getMultiSkipFeedback(trackId));

            entry.setValue(score);
        }
    }

    float calculateHistoryModifier(int historyRank) {
        return (float) Math.min(Math.pow(((historyRank - 20f) / 150), 3), 1);
    }

    void applyHistory(Map<Track, Float> ranking, UserProfile profile) {
        for (Map.Entry<Track, Float> entry : ranking.entrySet()) {
            Track t = entry.getKey();
            float score = entry.getValue();
            int historyRank = profile.getHistory(t.getId());
            if (historyRank > 0) {
                score *= calculateHistoryModifier(historyRank);
            }
            entry.setValue(score);
        }
    }
}
