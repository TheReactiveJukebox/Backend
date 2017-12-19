package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.recommendations.filters.ArtistPredicate;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stream<Track> filter(Stream<Track> trackStream) {
        Predicate<Track> artistPredicate = new ArtistPredicate(this);
        trackStream = trackStream.filter(artistPredicate);
        return trackStream;
    }

    @Override
    public ArtistPlain getPlainObject() {
        return new ArtistPlain(getName(), getId());
    }
}
