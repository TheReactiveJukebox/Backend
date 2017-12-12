package de.reactivejukebox.logger;

import de.reactivejukebox.model.IndirectFeedbackName;
import de.reactivejukebox.model.IndirectFeedbackPlain;
import de.reactivejukebox.model.User;

// indirect feedback

public class ActionFeedbackEntry extends Entry {
    public ActionFeedbackEntry(User user, IndirectFeedbackPlain feedback) {
        super(Event.ACTION_FEEDBACK, user);
        setValue(EntryCol.RADIO, feedback.getRadioId());
        setValue(EntryCol.USER_ACTION, feedback.getFeedbackName());
        setValue(EntryCol.SONG, feedback.getTrackId());
        if (feedback.getFeedbackName().equals(IndirectFeedbackName.MULTI_SKIP.toString())) {
            setValue(EntryCol.SONG_FORWARD, feedback.getToTrackId());
        }
        if (!feedback.getFeedbackName().equals(IndirectFeedbackName.DELETE.toString())) {
            setValue(EntryCol.SONG_RUNTIME, feedback.getPosition());
        }
    }
}
