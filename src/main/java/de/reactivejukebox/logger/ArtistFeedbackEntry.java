package de.reactivejukebox.logger;

import de.reactivejukebox.model.ArtistFeedback;
import de.reactivejukebox.model.User;

public class ArtistFeedbackEntry extends Entry {
    public ArtistFeedbackEntry(User user, ArtistFeedback feedback) {
        super(Event.ARTIST_FEEDBACK, user);
        // TODO set feedback values
    }
}
