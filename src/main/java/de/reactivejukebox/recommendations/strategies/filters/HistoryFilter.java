package de.reactivejukebox.recommendations.strategies.filters;

import de.reactivejukebox.model.HistoryEntry;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.User;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HistoryFilter {
    public static Stream<Track> filterHistory(Stream<Track> possibleTracks, User user) {
        Set<Track> history = Collections.EMPTY_SET;
        try {
            history =  Model.getInstance()
                    .getHistoryEntries()
                    .getListByUserId(user.getId())
                    .stream()
                    .map(HistoryEntry::getTrack)
                    .collect(Collectors.toSet());
        } catch (SQLException e) {
            System.err.println("Could not get history for user " + user.getId() +
                    " (" + user.getUsername() + "). Exception: ");
            e.printStackTrace();
            System.err.println("Running algorithm without history awareness.");
        }
        //TODO fix empty possible Tracks if History is to big.
        Set<Track> finalHistory = history;
        return possibleTracks.filter(track -> !finalHistory.contains(track));
    }
}

