package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.UserProfile;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.RecommendationStrategyFactory;
import de.reactivejukebox.recommendations.Recommendations;

import java.util.*;

public class HybridStrategy implements RecommendationStrategy {

    /**
     * Values roughly proportional to how much each of these actions influence recommendation score
     */
    enum FeedbackModifier {
        LIKE_TRACK(2f),
        DISLIKE_TRACK(0.1f),
        LIKE_ARTIST(1.25f),
        DISLIKE_ARTIST(0.75f),
        LIKE_ALBUM(1.25f),
        DISLIKE_ALBUM(0.75f),
        LIKE_TEMPO(1.25f),
        DISLIKE_TEMPO(0.75f),
        LIKE_MOOD(1.25f),
        DISLIKE_MOOD(0.75f),
        GENRE(0.25f),          // see calculateGenreModifier for tweaking
        GENRE_MAGNITUDE(3f),   // see calculateGenreModifier for tweaking
        SKIP(1.8f),            // see calculateLinearModifier for tweaking
        DELETE(1f),            // see calculateLinearModifier for tweaking
        MULTISKIP(0.7f),       // see calculateLinearModifier for tweaking
        FILTER_MISMATCH(4f);   // see calculateLinearModifier for tweaking

        public float value;

        FeedbackModifier(float value) {
            this.value = value;
        }
    }

    /**
     * How many recommendations every algorithm should generate
     */
    static final int N_BEST_SONGS = 200;

    private RecommendationStrategyFactory factory;
    private UserProfile userProfile;
    private boolean respectUserProfile = false;
    private Set<String> radioGenres;
    private Radio radio;
    private int resultCount;

    public HybridStrategy(RecommendationStrategyFactory factory, Radio radio, UserProfile userProfile, int resultCount) {
        this.factory = factory;
        this.resultCount = resultCount;
        this.radio = radio;

        // in reality, these shouldn't be null, just in case
        if (radio == null || radio.getGenres() == null) {
            radioGenres = Collections.emptySet();
        } else {
            radioGenres = new HashSet<>(Arrays.asList(radio.getGenres()));
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
            try {
                gatherAlgorithmResults(results, strategy);
            } catch (Exception e) {
                System.err.println("Error Executing Strategy: " + strategy);
                e.printStackTrace();
            }
        }

        // modify Ranking
        if (respectUserProfile) {
            applyUserFeedback(results, userProfile);
            applyHistory(results, userProfile);
            // apply radio settings (previously filters)
            for (Track t : results.keySet()) {
                results.put(t, results.get(t) * getFilterScore(t));
            }
        }

        // finally, collect tracks and sort them by score
        ArrayList<Track> recommendations = new ArrayList<>();
        recommendations.addAll(results.keySet());
        recommendations.sort(Comparator.comparing(results::get).reversed());

        // If need be, we could also assemble a list of scores like this:
        ArrayList<Float> scores = new ArrayList<>();
        for (Track t : recommendations) {
            scores.add(results.get(t));
        }

        /* and then...

        return new Recommendations(recommendations, scores);

        * But that's not necessary at the moment, so we save those CPU cycles.
        * If you ever end up here debugging a NullPointerException when accessing
        * Recommendations.getScores(), you know what to do.
        */

        return new Recommendations(recommendations.subList(0, Math.min(resultCount, recommendations.size())), scores);
    }


    /**
     * Instantiates and calls an algorithm to get its recommendations.
     *
     * @param results  the intermediate data structure used to keep track of scores in getRecommendations()
     * @param strategy the algorithm to execute
     */
    void gatherAlgorithmResults(Map<Track, Float> results, StrategyType strategy) {
        RecommendationStrategy algorithm = factory.createStrategy(strategy, N_BEST_SONGS);
        Recommendations algorithmResults = algorithm.getRecommendations();

        // iterate over tracks and scores simultaneously
        Iterator<Track> trackIterator = algorithmResults.getTracks().iterator();
        Iterator<Float> scoreIterator = algorithmResults.getScores().iterator();

        while (trackIterator.hasNext() && scoreIterator.hasNext()) {
            Track track = trackIterator.next();
            float score = scoreIterator.next();

            // compute final track score considering algorithm weight and add track to results
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
     * "Sigmoid-esque" function to map negative genre scores to modifier values between 1 and (1 - g)
     * and positive scores to values between 1 and (1 + g) where g = FeedbackModifier.GENRE.value.
     * <p>
     * FeedbackModifier.GENRE_MAGNITUDE.value (m) is used to tune the gradient of the function, i.e. how
     * fast it approaches 1 +/- g. For m == 3, the maximum (resp. minimum) modifier is approximately reached
     * when genre score >= 6 (resp. <= -6). This translates to the worst possible track score when the
     * balance of liked vs disliked genres is >= 6 (6 more disliked genres than liked ones).
     *
     * @param genreScore balance of liked vs disliked genres
     * @return the final modifier to apply to the track score
     */
    float calculateGenreModifier(int genreScore) {
        return (float) Math.tanh((float) genreScore / FeedbackModifier.GENRE_MAGNITUDE.value) / (1f / FeedbackModifier.GENRE.value) + 1f;
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

            if (profile.getSpeedFeedback(t.getfSpeed()) == 1) {
                score *= FeedbackModifier.LIKE_TEMPO.value;
            } else if (profile.getSpeedFeedback(t.getfSpeed()) == -1) {
                score *= FeedbackModifier.DISLIKE_TEMPO.value;
            }

            if (profile.getMoodFeedback(t.getfMood()) == 1) {
                score *= FeedbackModifier.LIKE_MOOD.value;
            } else if (profile.getMoodFeedback(t.getfMood()) == -1) {
                score *= FeedbackModifier.DISLIKE_MOOD.value;
            }

            // count liked vs. disliked genres, map to modifier
            score *= calculateGenreModifier(t.getGenres()
                    .stream()
                    .mapToInt(profile::getGenreFeedback)
                    .sum());

            // apply indirect feedback modifiers: how often was the track skipped, deleted, skipped over
            score *= calculateLinearModifier(FeedbackModifier.SKIP, profile.getSkipFeedback(trackId));
            score *= calculateLinearModifier(FeedbackModifier.DELETE, profile.getDeleteFeedback(trackId));
            score *= calculateLinearModifier(FeedbackModifier.MULTISKIP, profile.getMultiSkipFeedback(trackId));

            entry.setValue(score);
        }
    }

    /**
     * Function: ((historyRank - 20)/150)^3
     * the history modifier is:
     * 0 for the last 50 Songs played
     * ca. 0.15 for Song #100
     * ca. 0.66 for Song #150
     * 1 for Songs played >= 170 Songs ago
     *
     * @param historyRank # of songs since the song was last played
     * @return the history modifier value
     */
    float calculateHistoryModifier(int historyRank) {
        return (float) Math.min(Math.pow(((historyRank - 20f) / 150), 3), 1);
    }

    /**
     * Calculates the weight based on the time since the song was last played.
     *
     * @param ranking the intermediate data structure used to keep track of scores in getRecommendations()
     * @param profile the profile containing all of the user's feedback and the history of the current Radiostation
     */
    void applyHistory(Map<Track, Float> ranking, UserProfile profile) {
        for (Map.Entry<Track, Float> entry : ranking.entrySet()) {
            Track t = entry.getKey();
            float score = entry.getValue();
            int historyRank = profile.getHistory(t.getId());
            //Check if Track is in History
            if (historyRank > 0) {
                //Reduce score according to history modifier function
                score *= calculateHistoryModifier(historyRank);
            }
            entry.setValue(score);
        }
    }

    float getFilterScore(Track t) {
        final FeedbackModifier mod = FeedbackModifier.FILTER_MISMATCH;
        float score = 1.0f;
        int d1;
        int d2;

        // start and end year
        if (t.getReleaseDate() == null) {
            score *= 0.5;
        } else {
            int sy = radio.getStartYear() == null ? 0 : radio.getStartYear();
            int ey = radio.getEndYear() == null ? Integer.MAX_VALUE : radio.getEndYear();

            d1 = t.getReleaseDate().getYear() - sy;
            d2 = t.getReleaseDate().getYear() - ey;

            if (d1 < 0 || d2 > 0) {
                score *= calculateLinearModifier(mod, Math.min(Math.abs(d1), Math.abs(d2)));
            }
        }
        // minimum and maximum tempo
        float minTempo = radio.getMinSpeed() == null ? 0 : radio.getMinSpeed();
        float maxTempo = radio.getMaxSpeed() == null ? Float.MAX_VALUE : radio.getMaxSpeed();

        d1 = Math.round(t.getSpeed()) - Math.round(minTempo);
        d2 = Math.round(t.getSpeed()) - Math.round(maxTempo);

        if (d1 < 0 || d2 > 0) {
            score *= calculateLinearModifier(mod, Math.min(Math.abs(d1), Math.abs(d2)));
        }

        // arousal and valence
        if (!(radio.getArousal() == null || radio.getValence() == null)) {
            double a = radio.getArousal() - t.getArousal();
            double v = radio.getValence() - t.getValence();
            float distance = (float) Math.sqrt(a * a + v * v);
            distance = 1 - distance / 1.415f; // maximum distance in unit square is sqrt(2) = 1.4142...
            distance = 0.7f + 0.3f * distance; // arousal and valence data is quite inaccurate
            score *= distance;
        }

        // genres
        float genreScore = 1.0f;
        for (String genre : t.getGenres()) {
            if (radioGenres.contains(genre)) {
                genreScore += 0.05f;
            } else {
                genreScore -= 0.05f;
            }
        }
        score *= genreScore;

        return score;
    }
}
