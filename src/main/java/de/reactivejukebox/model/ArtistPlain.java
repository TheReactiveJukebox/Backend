package de.reactivejukebox.model;

public class ArtistPlain implements MusicEntityPlain {

    protected String name;
    protected int id;
    protected ArtistFeedback feedback;

    public ArtistPlain() {
    }

    public ArtistPlain(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArtistFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(ArtistFeedback feedback) {
        this.feedback = feedback;
    }
}
