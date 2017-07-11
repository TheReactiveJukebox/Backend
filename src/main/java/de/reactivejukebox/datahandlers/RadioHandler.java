package de.reactivejukebox.datahandlers;

import de.reactivejukebox.model.*;

import java.sql.SQLException;
import java.util.*;

public class RadioHandler {
    private Radios radios;
    private HistoryEntries historyEntries;
    private Tracks tracks;
    private Random random;

    public RadioHandler() {
        radios = Model.getInstance().getRadios();
        historyEntries = Model.getInstance().getHistoryEntries();
        tracks = Model.getInstance().getTracks();
        random = new Random();
    }

    public RadioPlain getRadiostation(User user) throws SQLException {
        return radios.getByUserId(user.getId()).getPlainObject();
    }

    public RadioPlain addRadiostation(RadioPlain radio, User user) throws SQLException {
        radio.setUserId(user.getId());
        return radios.put(radio).getPlainObject();
    }

    public List<TrackPlain> getSongs(int count, User user) throws SQLException {
        Radio radio = radios.getByUserId(user.getId());
        if (radio.isRandom()) {
            ArrayList<HistoryEntry> history = historyEntries.getListByRadioId(radio.getId());
            ArrayList<Track> tracksList;
            Collection<Track> usedTracks = new ArrayList<Track>();
            for (HistoryEntry entry : history) {
                usedTracks.add(entry.getTrack());
            }
            ArrayList<Track> randomTracks = new ArrayList<>();
            int i = 0;
            while (randomTracks.size() < count) {

                randomTracks = pickSample(tracks, count);

                if (i < 5) {
                    randomTracks.removeAll(usedTracks);
                } else {
                    //TODO clear History
                }
            }
            randomTracks.subList(0, count - 1);
            List<TrackPlain> result= new ArrayList<>();
            for (Track t :randomTracks){
                result.add((TrackPlain) t.getPlainObject());
            }
            return result;
        } else {
            return new ArrayList<TrackPlain>();
        }
    }

    private ArrayList<Track> pickSample(Tracks population, int nSamplesNeeded) {
        ArrayList<Track> list = new ArrayList<>();
        Iterator<Track> iter = population.iterator();
        int nLeft = population.size();
        while (nSamplesNeeded > 0) {
            int rand = random.nextInt(nLeft);
            if (iter.hasNext()) {
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
