package de.reactivejukebox.logger;

import de.reactivejukebox.model.TrackFeedback;
import de.reactivejukebox.model.User;

public class SongFeedbackEntry extends Entry {
    public SongFeedbackEntry(User user, TrackFeedback feedback) {
        super(Event.SONG_FEEDBACK, user);
        setValue(EntryCol.SONG, feedback.getTrackId());
        setValue(EntryCol.RATING_SONG, feedback.getSongFeedback());
        setValue(EntryCol.JSON, feedback);
    }
}
