package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HistoryFilter {
    private int resultCount;
    private Predicate<Track> history;

    public HistoryFilter(Radio radio, Collection<Track> upcoming, int resultCout) {
        this(Model.getInstance().getHistoryEntries(), radio, upcoming, resultCout);
    }

    public HistoryFilter(HistoryEntries historyEntries, Radio radio, Collection<Track> upcoming, int resultCount) {
        this.history = new HistoryPredicate(historyEntries, radio, upcoming);
        this.resultCount = resultCount;
    }



    public Stream<Track> forHistory(Stream<Track> trackStream) {
        //filter for History
        List<Track> allTracks = trackStream.collect(Collectors.toList());
        Set<Track> trackSet = allTracks.stream().filter(history).collect(Collectors.toSet());  // filter History
        if (trackSet.size() >= resultCount) {
            return trackSet.stream();       //result filtered for History
        } else {
            return allTracks.stream();             //result with already used tracks
        }
    }


}
