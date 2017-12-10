package de.reactivejukebox.logger;

import de.reactivejukebox.model.AlbumFeedback;
import de.reactivejukebox.model.User;

public class AlbumFeedbackEntry extends Entry {
    public AlbumFeedbackEntry(User user, AlbumFeedback feedback) {
        super(Event.ALBUM_FEEDBACK, user);
        // TODO set feedback values
    }
}
