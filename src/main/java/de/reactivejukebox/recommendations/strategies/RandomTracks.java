package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class RandomTracks implements RecommendationStrategy {

    private Collection<Track> history;
    private int resultCount;
    private static Random random = new Random();

    public RandomTracks(Collection<Track> history, int resultCount) {
        this.history = history;
        this.resultCount=resultCount;
    }

    @Override
    public List<Track> getRecommendations() {
        List<Track> possibleTracks = Model.getInstance().getTracks().stream()
                .filter(track -> !history.contains(track)) // ignore recent history
                .collect(Collectors.toList()); // collect into list

        return pickSample(possibleTracks, resultCount);

    }

    private ArrayList<Track> pickSample(List<Track> population, int nSamplesNeeded) {
        ArrayList<Track> list = new ArrayList<>();
        Iterator<Track> iter = population.iterator();
        int nLeft = population.size();
        while (nSamplesNeeded > 0) {
            int rand = random.nextInt(nLeft);//random number between 0 and nLeft (=population.size())
            if (iter.hasNext()) { //song available
                if (rand < nSamplesNeeded) {
                    list.add(iter.next());
                    nSamplesNeeded--;
                } else {
                    iter.next();
                }
            }
            nLeft--;
        }
        return list;
    }
}
