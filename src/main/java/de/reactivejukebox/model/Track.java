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
    Date date;

    public Track(int id, String title, Artist artist, Album album, String cover, String hash, int duration, int playCount, Date date) {
        this();
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.cover = cover;
        this.duration = duration;
        this.hash = hash;
        this.playCount = playCount;
        this.date = date;
    }

    public Track(){genres = new ArrayList<>();}

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

    public Album getAlbum() {return album; }

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

    public void setGenres(List<String> genres) { this.genres = genres; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    @Override
    public MusicEntityPlain getPlainObject() {
        String file = hash.substring(0, 1) + "/" + hash.substring(1, 2) + "/" + hash.substring(2) + ".mp3";
        return new TrackPlain(id, title, artist.getId(), album.getId(), cover, file, duration, playCount, genres, date);
    }
}
