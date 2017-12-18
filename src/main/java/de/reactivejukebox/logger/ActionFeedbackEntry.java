package de.reactivejukebox.logger;

import de.reactivejukebox.model.IndirectFeedbackName;
import de.reactivejukebox.model.IndirectFeedbackPlain;
import de.reactivejukebox.model.User;

// indirect feedback

public class ActionFeedbackEntry extends Entry {
    public ActionFeedbackEntry(User user, IndirectFeedbackPlain feedback) {
        super(Event.ACTION_FEEDBACK, user);
        setValue(EntryCol.RADIO, feedback.getRadioId());
        setValue(EntryCol.SONG, feedback.getTrackId());
        String name = feedback.getFeedbackName();
        setValue(EntryCol.USER_ACTION, name);
        if (name != null) {
            if (name.equals(IndirectFeedbackName.MULTI_SKIP.toString())) {
                setValue(EntryCol.SONG_FORWARD, feedback.getToTrackId());
            }
            if (!name.equals(IndirectFeedbackName.DELETE.toString())) {
                setValue(EntryCol.SONG_RUNTIME, feedback.getPosition());
            }
        }
        setValue(EntryCol.JSON, feedback);
    }
}
