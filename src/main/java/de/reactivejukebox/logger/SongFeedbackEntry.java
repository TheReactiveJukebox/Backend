package de.reactivejukebox.logger;

import de.reactivejukebox.model.TrackFeedback;
import de.reactivejukebox.model.User;

public class SongFeedbackEntry extends Entry {
    public SongFeedbackEntry(User user, TrackFeedback feedback) {
        super(Event.SONG_FEEDBACK, user);
        setValue(EntryCol.SONG, feedback.getTrack().getId());
        setValue(EntryCol.RATING_DYNAMIC, feedback.getDynamicsFeedback());
        setValue(EntryCol.RATING_MOOD, feedback.getMoodFeedback());
        setValue(EntryCol.RATING_SPEED, feedback.getSpeedFeedback());
        setValue(EntryCol.RATING_SONG, feedback.getSongFeedback());
    }
}
