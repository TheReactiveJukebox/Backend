package de.reactivejukebox.logger;

import de.reactivejukebox.model.IndirectFeedbackPlain;
import de.reactivejukebox.model.User;

public class ActionFeedbackEntry extends Entry {
    public ActionFeedbackEntry(User user, IndirectFeedbackPlain feedback) {
        super(Event.ACTION_FEEDBACK, user);
        // TODO set feedback values
    }
}
