package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;

public class Artist implements MusicEntity {

    protected int id;
    protected String name;

    public Artist() {
    }

    public Artist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameNormalized() {
        return DatabaseProvider.getInstance().getDatabase().normalize(getName());
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
