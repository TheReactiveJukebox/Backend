package de.reactivejukebox.logger;

import de.reactivejukebox.model.ArtistFeedback;
import de.reactivejukebox.model.User;

public class ArtistFeedbackEntry extends Entry {
    public ArtistFeedbackEntry(User user, ArtistFeedback feedback) {
        super(Event.ARTIST_FEEDBACK, user);
        setValue(EntryCol.ARTIST, feedback.getArtist());
        setValue(EntryCol.RATING_SONG, feedback.getFeedback());
        setValue(EntryCol.JSON, feedback);
    }
}
