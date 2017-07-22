package de.reactivejukebox.recommendations.strategies.traits;

import de.reactivejukebox.model.HistoryEntry;
import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.User;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class HistoryAwareness {
    public static Set<Track> recentHistory(User user) {
        try {
            return Model.getInstance()
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
            return Collections.emptySet();
        }
    }
}
