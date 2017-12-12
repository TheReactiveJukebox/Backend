package de.reactivejukebox.logger;

import de.reactivejukebox.model.GenreFeedback;
import de.reactivejukebox.model.User;

public class GenreFeedbackEntry extends Entry {
    public GenreFeedbackEntry(User user, GenreFeedback feedback) {
        super(Event.GENRE_FEEDBACK, user);
        setValue(EntryCol.GENRE, feedback.getGenre());
        setValue(EntryCol.RATING, feedback.getFeedback());
    }
}
