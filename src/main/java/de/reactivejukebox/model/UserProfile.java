package de.reactivejukebox.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

public class UserProfile {
    private Tracks tracks;
    private Radio radio;

    private HashSet<TrackFeedback> rawTrackFeedback;
    private HashMap<Integer, Integer> trackFeedback;
    private HashMap<Integer, Integer> artistFeedback;
    private HashMap<Integer, Integer> albumFeedback;
    private HashMap<String, Integer> genreFeedback;

    private HashMap<Integer, Integer> speedFeedback;
    private HashMap<MoodKey, Integer> moodFeedback;

    private HashMap<Integer, Integer> skipFeedback;
    private HashMap<Integer, Integer> deleteFeedback;
    private HashMap<Integer, Integer> multiSkipFeedback;



    public UserProfile(Radio radio) throws SQLException {
        this.radio = radio;
        int userId = radio.getUser().getId();
        tracks = Model.getInstance().getTracks();
        rawTrackFeedback = Model.getInstance().getTrackFeedbacks().getByUserId(userId);
        SpecialFeedbacks sf = Model.getInstance().getSpecialFeedbacks();
        artistFeedback = sf.getArtistFeedback(userId);
        albumFeedback = sf.getAlbumFeedback(userId);
        genreFeedback = sf.getGenreFeedback(userId);


        skipFeedback = Model.getInstance().getIndirectFeedbackEntries().getSkipFeedback(radio.getId(),userId);
        deleteFeedback = Model.getInstance().getIndirectFeedbackEntries().getDeleteFeedback(radio.getId(),userId);
        multiSkipFeedback = Model.getInstance().getIndirectFeedbackEntries().getMultiSkipFeedback(radio.getId(),userId);


        speedFeedback = new HashMap<>();
        trackFeedback = new HashMap<>();
        moodFeedback = new HashMap<>();
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

    public int getSpeedFeedback(float speed){
        int speedInt = Math.round(speed/5); //TODO change with new Feedback
        return speedFeedback.getOrDefault(speedInt, 0);
    }

    public int getMoodFeedback(float arousal, float valence){
        MoodKey key = new MoodKey(arousal,valence);
        return moodFeedback.getOrDefault(key, 0);
    }



    //TODO change with new Feedback
    private void build(){
        for (TrackFeedback f: rawTrackFeedback) {
            Track t = tracks.get(f.getTrackId());
            if (f.getSongFeedback()> 0){
                trackFeedback.put(t.getId(), 1);
            }else if (f.getSongFeedback()< 0){
                trackFeedback.put(t.getId(),-1);
            }
            if (f.getSpeedFeedback() > 0){
                float speed = t.getSpeed()/5;
                speedFeedback.put(Math.round(speed),1);
            }else if (f.getSpeedFeedback() < 0) {
                float speed = t.getSpeed()/5;
                speedFeedback.put(Math.round(speed),-1);
            }
            if (f.getMoodFeedback() > 0) {
                moodFeedback.put(new MoodKey(t.getArousal(), t.getValence()),1);
            } else if (f.getMoodFeedback() < 0) {
                moodFeedback.put(new MoodKey(t.getArousal(), t.getValence()),-1);
            }
        }


    }
}