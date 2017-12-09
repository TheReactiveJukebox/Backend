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

    public int getTrackFeedback(int trackId){
        if (trackFeedback.containsKey(trackId)) {
            return trackFeedback.get(trackId);
        } else {
            return 0;
        }
    }

    public int getArtistFeedback(int artistId){
        if (artistFeedback.containsKey(artistId)) {
            return artistFeedback.get(artistId);
        } else {
            return 0;
        }
    }

    public int getAlbumFeedback(int id){
        if (albumFeedback.containsKey(id)) {
            return albumFeedback.get(id);
        } else {
            return 0;
        }
    }

    public int getGenreFeedback(String id){
        if (genreFeedback.containsKey(id)) {
            return genreFeedback.get(id);
        } else {
            return 0;
        }
    }

    public int getSpeedFeedback(float speed){
        int speedInt = Math.round(speed/5); //TODO change with new Feedback
        if (speedFeedback.containsKey(speedInt)) {
            return speedFeedback.get(speedInt);
        } else {
            return 0;
        }
    }

    public int getMoodFeedback(float arousal, float valence){
        MoodKey key = new MoodKey(arousal,valence);
        if (moodFeedback.containsKey(key)) {
            return moodFeedback.get(key);
        } else {
            return 0;
        }
    }

    public int isTrackLiked(int id){
        return trackFeedback.get(id) > 0 ? 1 : 0;
    }

    public int isTrackDisLiked(int id){
        return trackFeedback.get(id) < 0 ? 1 : 0;
    }

    public int isArtistLiked(int id){
        return artistFeedback.get(id) > 0 ? 1 : 0;
    }

    public int isArtistDisLiked(int id){
        return artistFeedback.get(id) < 0 ? 1 : 0;
    }

    public int isAlbumLiked(int id){
        return albumFeedback.get(id) > 0 ? 1 : 0;
    }

    public int isAlbumDisLiked(int id){
        return albumFeedback.get(id) < 0 ? 1 : 0;
    }

    public int isGenreLiked(String id){
        return genreFeedback.get(id) > 0 ? 1 : 0;
    }

    public int isGenreDisLiked(String id){
        return genreFeedback.get(id) < 0 ? 1 : 0;
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