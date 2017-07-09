package de.reactivejukebox.model;

public class Artist implements MusicEntity {

    protected int id;
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public MusicEntityPlain getPlainObject() {
        return new ArtistPlain(getName(), getId());
    }
}
