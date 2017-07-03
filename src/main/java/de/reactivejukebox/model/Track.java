package de.reactivejukebox.model;

public class Track {

    int id; //global track id
    String title;
    String artist;
    String album;
    String cover;
    int duration; //song duration in seconds
    String hash;

    public Track(int id, String title, String artist, String album, String cover, int duration, String hash) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.cover = cover;
        this.duration = duration;
        this.hash = hash;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
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

    public String getHash() { return hash; }

    public void setHash(String hash) { this.hash = hash; }



}
