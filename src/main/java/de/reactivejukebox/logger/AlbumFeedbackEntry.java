package de.reactivejukebox.logger;

import de.reactivejukebox.model.AlbumFeedback;
import de.reactivejukebox.model.User;

public class AlbumFeedbackEntry extends Entry {
    public AlbumFeedbackEntry(User user, AlbumFeedback feedback) {
        super(Event.ALBUM_FEEDBACK, user);
        setValue(EntryCol.ALBUM, feedback.getAlbum());
        setValue(EntryCol.RATING, feedback.getFeedback());
    }
}
