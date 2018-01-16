package de.reactivejukebox.model;

import java.sql.SQLException;
import java.util.*;

public class UserProfile {
    private Tracks tracks;
    private Radio radio;

    private ArrayList<HistoryEntry> historyList;
    private HashMap<Integer, Integer> trackFeedback;
    private HashMap<Integer, Integer> artistFeedback;
    private HashMap<Integer, Integer> albumFeedback;
    private HashMap<String, Integer> genreFeedback;

    private HashMap<Integer, Integer> speedFeedback;
    private HashMap<Integer, Integer> moodFeedback;

    private HashMap<Integer, Integer> skipFeedback;
    private HashMap<Integer, Integer> deleteFeedback;
    private HashMap<Integer, Integer> multiSkipFeedback;

    private HashMap<Integer, Integer> history;



    public UserProfile(Radio radio) throws SQLException {
        this.radio = radio;
        int userId = radio.getUser().getId();
        tracks = Model.getInstance().getTracks();
        trackFeedback = Model.getInstance().getTrackFeedbacks().getByUserId(userId);
        SpecialFeedbacks sf = Model.getInstance().getSpecialFeedbacks();
        artistFeedback = sf.getArtistFeedback(userId);
        albumFeedback = sf.getAlbumFeedback(userId);
        genreFeedback = sf.getGenreFeedback(userId);


        skipFeedback = Model.getInstance().getIndirectFeedbackEntries().getSkipFeedback(radio.getId(),userId);
        deleteFeedback = Model.getInstance().getIndirectFeedbackEntries().getDeleteFeedback(radio.getId(),userId);
        multiSkipFeedback = Model.getInstance().getIndirectFeedbackEntries().getMultiSkipFeedback(radio.getId(),userId);

        historyList = Model.getInstance().getHistoryEntries().getListByRadioId(radio.getId());

        history = new HashMap<>();
        speedFeedback = Model.getInstance().getSpecialFeedbacks().getSpeedFeedback(userId);
        trackFeedback = Model.getInstance().getTrackFeedbacks().getByUserId(userId);
        moodFeedback = Model.getInstance().getSpecialFeedbacks().getMoodFeedback(userId);
        build();
    }

    public int getTrackFeedback(int trackId){
        return trackFeedback.getOrDefault(trackId, 0);
    }

    public int getArtistFeedback(int artistId){
        return artistFeedback.getOrDefault(artistId, 0);
    }

    public int getAlbumFeedback(int id){
        return albumFeedback.getOrDefault(id, 0);
    }

    public int getGenreFeedback(String id){
        return genreFeedback.getOrDefault(id, 0);
    }

    public int getSkipFeedback(int id){
        return skipFeedback.getOrDefault(id, 0);
    }

    public int getDeleteFeedback(int id){
        return deleteFeedback.getOrDefault(id, 0);
    }

    public int getMultiSkipFeedback(int id){
        return multiSkipFeedback.getOrDefault(id, 0);
    }

    public int getSpeedFeedback(int fSpeed){
        return speedFeedback.getOrDefault(fSpeed, 0);
    }

    public int getMoodFeedback(int fMood){
        return moodFeedback.getOrDefault(fMood, 0);
    }

    public int getHistory(int trackId){ return history.getOrDefault(trackId,0); }


    //TODO change with new Feedback
    private void build(){
        historyList.sort(Comparator.comparing(HistoryEntry::getTime));
        ListIterator<HistoryEntry> li = historyList.listIterator(historyList.size());
        int i = 1;
        while (li.hasPrevious()){
            HistoryEntry entry = li.previous();
            history.put(entry.getTrack().getId(), i);
            i++;
        }
    }
}