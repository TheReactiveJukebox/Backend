package de.reactivejukebox.model;

public class Track {

    int id; //global track id
    String title;
    Artist artist;
    String album;
    String cover;
    int duration; //song duration in seconds
    String hash;
    String file;

    public Track(int id, String title, Artist artist, String album, String cover, int duration, String hash) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.cover = cover;
        this.duration = duration;
        this.hash = hash;
        if(hash.length()>2) {
            this.file = hash.substring(0, 1) + "/" + hash.substring(1, 2) + "/" + hash.substring(2) + ".mp3";
        }
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

    public String getFile() { return file; }

    public void setFile(String file) { this.file = file; }

}
