package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.Tracks;
import de.reactivejukebox.recommendations.RecommendationStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class RandomTracks implements RecommendationStrategy {

    private Collection<Track> history;
    private int resultCount;
    private static Random random = new Random();
    private Tracks tracks;

    public RandomTracks(Collection<Track> history, int resultCount) {
        this(history, resultCount, Model.getInstance().getTracks());
    }

    public RandomTracks(Collection<Track> history, int resultCount, Tracks tracks) {
        this.history = history;
        this.resultCount = resultCount;
        this.tracks = tracks;
    }

    @Override
    public List<Track> getRecommendations() {

        List<Track> possibleTracks = tracks.stream()
                .filter(track -> !history.contains(track)) // ignore recent history
                .collect(Collectors.toList()); // collect into list

        if (possibleTracks.size() >= resultCount) { //enough tracks without history available
            return pickSample(possibleTracks, resultCount);
        } else { //not enough tracks without history available
            possibleTracks = tracks.stream() //include history tracks
                    .collect(Collectors.toList()); // collect into list

            if (possibleTracks.size() >= resultCount) { //enough tracks with history tracks available
                return pickSample(possibleTracks, resultCount);
            } else { //more songs requested than in database
                ArrayList<Track> pickedTracks = pickSample(possibleTracks, resultCount); //pick possible tracks w/ history
                //pick the remaining tracks from alredy picked tracks. Tracks will be added multiple times
                while (resultCount - pickedTracks.size() > 0) {
                    pickedTracks.addAll(pickSample(possibleTracks, (resultCount - pickedTracks.size())));
                }
                return pickedTracks; //return both picks
            }
        }

    }

    /**
     * Choice randomly resultCount differ tracks from population and return them as list.
     * If resultCount greater then count of tracks in population add the whole population once.
     * Resulting list size can be lower than population if resultCount>population.size()
     */
    private ArrayList<Track> pickSample(List<Track> population, final int resultCount) {
        ArrayList<Track> list = new ArrayList<Track>();
        if (resultCount >= population.size()) {
            list.addAll(population); //add all, no random needed
        } else if (list.size() < resultCount) {
            int nLeft = resultCount - list.size();
            HashSet<Track> selectedItems = new HashSet<Track>();
            // choice resultCount different tracks
            while (selectedItems.size() < nLeft) {
                // get random number between 0 and population.size()
                int rand = random.nextInt(population.size());
                // get track on position rand
                Track track = population.get(rand);
                // add track in result set
                selectedItems.add(track);
            }
            list.addAll(selectedItems);
        }
        // shuffle result list
        Collections.shuffle(list, random);

        return list;
    }
}
