package de.reactivejukebox.recommendations.traits;
import de.reactivejukebox.model.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HistoryPredicate implements Predicate<Track> {
    private Set<Track>finalHistory;

    public HistoryPredicate(Radio radio,Collection<Track> upcoming){
        this(Model.getInstance().getHistoryEntries(),radio,upcoming);
    }

    public HistoryPredicate(HistoryEntries history, Radio radio, Collection<Track> upcoming){
        Set<Track> tracks = Collections.EMPTY_SET;
        User user = radio.getUser();
        try {
             tracks = history
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
        tracks.addAll(upcoming);
        //TODO limit history Size
        finalHistory = tracks;
    }

    @Override
    public boolean test(Track track) {
        return !finalHistory.contains(track);
    }
}
