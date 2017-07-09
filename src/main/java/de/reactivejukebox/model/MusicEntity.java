package de.reactivejukebox.model;

/**
 * Interface for rich music entities that need to be flattened in order to be send as JSON objects
 */
public interface MusicEntity {

    /**
     * Obtain a plain version of this MusicEntity that can be send as a JSON Object.
     * Sending, for example, the Artist and AlbumPlain objects embedded in a track object leaves
     * the question when to stop embedding more objects if there are cyclic references.
     * <p>
     * Therefore, the API should only return "dumb" versions of the MusicEntities where
     * references to other MusicEntities are replaced by their IDs.
     *
     * @return a plain version of this MusicEntity
     */
    MusicEntityPlain getPlainObject();
}
