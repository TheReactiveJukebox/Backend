package de.reactivejukebox.model;

import java.util.Date;
import java.util.List;

public class TrackPlain implements MusicEntityPlain {

    int id; //global track id
    String title;
    int artist;
    int album;
    String cover;
    int duration; //song duration in seconds
    String file;
    int playCount;
    List<String> genres;
    Date releaseDate;
    float speed;
    float dynamic;
    TrackFeedbackPlain trackFeedback;

    public TrackPlain(int id, String title, int artist, int album, String cover, String file, int duration, int playCount, List<String> genres, Date releaseDate, float speed, float dynamic) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.cover = cover;
        this.duration = duration;
        this.file = file;
        this.playCount = playCount;
        this.genres = genres;
        this.releaseDate = releaseDate;
        this.speed = speed;
        this.dynamic = dynamic;
    }

    public TrackPlain() {
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArtist() {
        return artist;
    }

    public void setArtist(int artist) {
        this.artist = artist;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDynamic() {
        return dynamic;
    }

    public void setDynamic(float dynamic) {
        this.dynamic = dynamic;
    }

    public TrackFeedbackPlain getTrackFeedback() {
        return trackFeedback;
    }

    public void setTrackFeedback(TrackFeedbackPlain trackFeedback) {
        this.trackFeedback = trackFeedback;
    }
}
