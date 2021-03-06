package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Returns TRUE if Track is not within the history of given radio
 */
public class HistoryPredicate implements Predicate<Track> {
    private Set<Track> finalHistory;

    public HistoryPredicate(Radio radio, Collection<Track> upcoming) {
        this(Model.getInstance().getHistoryEntries(), radio, upcoming);
    }

    public HistoryPredicate(Collection<Track> upcoming) {
        finalHistory = new HashSet<>();
        for (Track t : upcoming) {
            finalHistory.add(t);
        }
    }

    public HistoryPredicate(HistoryEntries history, Radio radio, Collection<Track> upcoming) {
        Set<Track> tracks = new HashSet<>();
        User user = radio.getUser();
        try {
            tracks = history
                    .getListByRadioId(radio.getId())
                    .stream()
                    .map(HistoryEntry::getTrack)
                    .collect(Collectors.toSet());
        } catch (SQLException e) {
            System.err.println("Could not get history for user " + user.getId() +
                    " (" + user.getUsername() + "). Exception: ");
            e.printStackTrace();
            System.err.println("Running algorithm without history awareness.");
        }
        tracks.addAll(upcoming);
        //TODO limit history Size
        finalHistory = tracks;
    }

    @Override
    public boolean test(Track track) {
        return !finalHistory.contains(track);
    }
}
