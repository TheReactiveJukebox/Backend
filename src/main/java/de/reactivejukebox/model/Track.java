package de.reactivejukebox.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Track implements MusicEntity {

    int id; //global track id
    String title;
    Artist artist;
    Album album;
    String cover;
    int duration; //song duration in seconds
    String hash;
    int playCount;
    List<String> genres;
    Date releaseDate;
    float speed;
    float dynamic;
    float valence;
    float arousal;


    public Track(int id, String title, Artist artist, Album album, String cover, String hash, int duration, int playCount, Date date, float speed, float dynamic) {
        this();
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.cover = cover;
        this.duration = duration;
        this.hash = hash;
        this.playCount = playCount;
        this.releaseDate = date;
        this.speed = speed;
        this.dynamic = dynamic;
    }

    public Track(int id, String title, Artist artist, Album album, String cover, String hash, int duration, int playCount, Date releaseDate, float speed, float dynamic, float valence, float arousal) {
        this(id, title, artist, album, cover, hash, duration, playCount, releaseDate, speed, dynamic);
        this.valence = valence;
        this.arousal = arousal;
    }

    public Track() {
        genres = new ArrayList<>();
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

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date date) {
        this.releaseDate = date;
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

    public float getValence() {
        return valence;
    }

    public void setValence(float valence) {
        this.valence = valence;
    }

    public float getArousal() {
        return arousal;
    }

    public void setArousal(float arousal) {
        this.arousal = arousal;
    }

    @Override
    public TrackPlain getPlainObject() {
        String file = hash.substring(0, 1) + "/" + hash.substring(1, 2) + "/" + hash.substring(2) + ".mp3";
        return new TrackPlain(id, title, artist.getId(), album.getId(), cover, file, duration, playCount, genres, releaseDate, speed, dynamic, arousal, valence);
    }

    @Override
    public String toString() {
        return "ID: " + this.getId() + ", " +
                "Title: " + this.getTitle() + ", " +
                "Artist: " + this.getArtist().getName() + " " + this.getArtist().getId() + ", " +
                "Arousal: " + this.getArousal() + ", " +
                "Valence: " + this.getValence() + ", ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        return id == track.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
