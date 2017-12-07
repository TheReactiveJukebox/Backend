package de.reactivejukebox.model;

import java.sql.SQLException;
import java.util.HashSet;

public class UserProfile {
    private Radio radio;
    private Tracks tracks;

    private HashSet<TrackFeedback> trackFeedback;
    private HashSet<ArtistFeedback> artistFeedback;
    private HashSet<AlbumFeedback> albumFeedback;
    private HashSet<GenreFeedback> genreFeedback;

    private HashSet<Integer> trackLikes;
    private HashSet<Integer> trackDislikes;
    private HashSet<Integer> artistLikes;
    private HashSet<Integer> artistDislikes;
    private HashSet<Integer> albumLikes;
    private HashSet<Integer> albumDislikes;
    private HashSet<String> genreLikes;
    private HashSet<String> genreDislikes;

    private HashSet<Integer> speedLikes;
    private HashSet<Integer> moodLikes;


    private Float arousal;
    private Float valence;
    private Float speed;



    public UserProfile(int userId, Radio radio) throws SQLException {
        tracks = Model.getInstance().getTracks();
        trackFeedback = Model.getInstance().getTrackFeedbacks().getByUserId(userId);
        SpecialFeedbacks sf = Model.getInstance().getSpecialFeedbacks();
        artistFeedback = sf.getArtistFeedback(userId);
        albumFeedback = sf.getAlbumFeedback(userId);
        genreFeedback = sf.getGenreFeedback(userId);
        this.radio = radio;
        sort();
        build();
    }







    private void sort(){
        for (TrackFeedback f: trackFeedback) {
            if (f.getSongFeedback()> 0){
                trackLikes.add(f.getTrackId());
            }else if (f.getSongFeedback()< 0){
                trackDislikes.add(f.getTrackId());
            }
            if (f.getSpeedFeedback() > 0){
                speedLikes.add(f.getTrackId());
            }
            if (f.getMoodFeedback() > 0) {
                moodLikes.add(f.getTrackId());
            }
        }
        for (ArtistFeedback f : artistFeedback){
            if (f.getFeedback() > 0){
                artistLikes.add(f.getArtist());
            }else if(f.getFeedback() < 0){
                artistDislikes.add(f.getArtist());
            }
        }
        for (AlbumFeedback f : albumFeedback){
            if (f.getFeedback() > 0){
                albumLikes.add(f.getAlbum());
            }else if(f.getFeedback() < 0){
                albumDislikes.add(f.getAlbum());
            }
        }
        for (GenreFeedback f : genreFeedback){
            if (f.getFeedback() > 0){
                genreLikes.add(f.getGenre());
            }else if(f.getFeedback() < 0){
                genreDislikes.add(f.getGenre());
            }
        }
    }
    private void build(){
        for(int i: speedLikes){
            speed += tracks.get(i).getSpeed();
        }
        speed = speed/speedLikes.size();
        for(int i: moodLikes){
            //arousal += tracks.get(i).getArousal();
            //valence += tracks.get(i).getValence();
        }
        arousal = arousal/moodLikes.size();
        valence = valence/moodLikes.size();
    }
}
