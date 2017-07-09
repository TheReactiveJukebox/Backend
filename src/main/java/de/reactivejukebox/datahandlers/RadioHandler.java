package de.reactivejukebox.datahandlers;

import de.reactivejukebox.model.*;

import java.sql.SQLException;
import java.util.*;

public class RadioHandler {
    private Radios radios;
    private HistoryEntries historyEntries;
    private Tracks tracks;

    public RadioHandler() {
        radios = Model.getInstance().getRadios();
        historyEntries = Model.getInstance().getHistoryEntries();
        tracks = Model.getInstance().getTracks();
    }

    public RadioPlain getRadiostation(User user) throws SQLException {
        return radios.getByUserId(user.getId()).getPlainObject();
    }

    public RadioPlain addRadiostation(RadioPlain radio, User user) throws SQLException {
        radio.setUserId(user.getId());
        return radios.put(radio).getPlainObject();
    }

    public List<Track> getSongs(int count, User user) throws SQLException {
        Radio radio = radios.getByUserId(user.getId());
        if (radio.isRandom()) {
            ArrayList<HistoryEntry> history = historyEntries.getListbyRadioId(radio.getId());
            Collection<Track> usedTracks = new ArrayList<>();
            for (HistoryEntry entry : history) {
                usedTracks.add(entry.getTrack());
            }
            ArrayList<Track> randomTracks = new ArrayList<>();
            int i = 0;
            while (randomTracks.size() < count) {
                randomTracks = tracks.getRandom(count);
                if (i < 5) {
                    randomTracks.removeAll(usedTracks);
                } else {
                    //TODO clear History
                }
            }
            return randomTracks.subList(0, count - 1);
        }else{
            return new ArrayList<Track>();
        }
    }
}
