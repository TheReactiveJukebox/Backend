package de.reactivejukebox.model;

public class TrackPlain implements MusicEntityPlain {

    int id; //global track id
    String title;
    int artist;
    int album;
    String cover;
    int duration; //song duration in seconds
    String file;

    public TrackPlain(int id, String title, int artist, int album, String cover, String file, int duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.cover = cover;
        this.duration = duration;
        this.file = file;
    }

    public TrackPlain(){}

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

    public String getFile() { return file; }

    public void setFile(String file) { this.file = file; }

}
