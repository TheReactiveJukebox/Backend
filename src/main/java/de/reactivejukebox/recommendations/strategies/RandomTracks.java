package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.Tracks;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.filters.HistoryFilter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomTracks implements RecommendationStrategy {

    private static Random random = new Random();
    private int resultCount;
    private Tracks tracks;
    private HistoryFilter historyFilter;
    private Collection<Track> upcoming;
    private Radio radio;


    public RandomTracks(Radio radio, Collection<Track> upcoming, int resultCount) {
        this(radio, upcoming, resultCount, Model.getInstance().getTracks());
    }

    public RandomTracks(Radio radio, Collection<Track> upcoming, int resultCount, Tracks tracks) {
        this.radio = radio;
        this.resultCount = resultCount;
        this.tracks = tracks;
        this.upcoming = upcoming;
        this.historyFilter = new HistoryFilter(radio, upcoming, resultCount);
    }

    @Override
    public List<Track> getRecommendations() {
        Stream<Track> trackStream = radio.filter(tracks.stream());
        List<Track> possibleTracks = historyFilter.forHistory(trackStream) // historyFilter by Radio properties and history
                .collect(Collectors.toList()); // collect into list

        if (possibleTracks.size() >= resultCount) { //enough tracks without history available
            return pickSample(possibleTracks, resultCount);
        } else { //not enough tracks by radio properties
            possibleTracks = tracks.stream() //include all Tracks
                    .collect(Collectors.toList()); // collect into list
            if (possibleTracks.size() >= resultCount) { //enough tracks available
                return pickSample(possibleTracks, resultCount);
            } else { //more songs requested than in database
                List<Track> pickedTracks = pickSample(possibleTracks, resultCount); //pick possible tracks w/ history
                //pick the remaining tracks from already picked tracks. Tracks will be added multiple times
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
    private List<Track> pickSample(List<Track> population, final int resultCount) {
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
