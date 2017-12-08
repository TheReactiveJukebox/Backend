package de.reactivejukebox.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

public class UserProfile {
    private Tracks tracks;

    private HashSet<TrackFeedback> rawTrackFeedback;
    private HashMap<Integer, Integer> trackFeedback;
    private HashMap<Integer, Integer> artistFeedback;
    private HashMap<Integer, Integer> albumFeedback;
    private HashMap<String, Integer> genreFeedback;

    private HashMap<Integer, Integer> speedFeedback;
    private HashMap<MoodKey, Integer> moodFeedback;


    private Float arousal;
    private Float valence;
    private Float speed;



    public UserProfile(int userId) throws SQLException {
        tracks = Model.getInstance().getTracks();
        rawTrackFeedback = Model.getInstance().getTrackFeedbacks().getByUserId(userId);
        SpecialFeedbacks sf = Model.getInstance().getSpecialFeedbacks();
        artistFeedback = sf.getArtistFeedback(userId);
        albumFeedback = sf.getAlbumFeedback(userId);
        genreFeedback = sf.getGenreFeedback(userId);

        speedFeedback = new HashMap<>();
        trackFeedback = new HashMap<>();
        moodFeedback = new HashMap<>();
        build();
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
                //TODO wait for arousal and valence
            } else if (f.getMoodFeedback() < 0) {

            }

        }

    }
}