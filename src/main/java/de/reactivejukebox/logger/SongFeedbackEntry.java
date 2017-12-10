package de.reactivejukebox.logger;

import de.reactivejukebox.model.TrackFeedback;
import de.reactivejukebox.model.User;

public class SongFeedbackEntry extends Entry {
    public SongFeedbackEntry(User user, TrackFeedback trackFeedback) {
        super(Event.SONG_FEEDBACK, user);
        // TODO set feedback values
    }
}
