package de.reactivejukebox.model;

public class Album implements MusicEntity {

    protected int id;
    protected String title;
    protected Artist artist;

    public Album() {
    }

    public Album(int id, String title, Artist artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public AlbumPlain getPlainObject() {
        AlbumPlain plainObject = new AlbumPlain();
        plainObject.setArtist(getArtist().getId());
        plainObject.setId(getId());
        plainObject.setTitle(getTitle());
        return plainObject;
    }
}
