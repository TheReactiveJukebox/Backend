package de.reactivejukebox.logger;

import de.reactivejukebox.model.User;

public class QuestionnaireEntry extends Entry {
    public QuestionnaireEntry(User user) {
        super(Event.QUESTIONNAIRE, user);
    }
}
