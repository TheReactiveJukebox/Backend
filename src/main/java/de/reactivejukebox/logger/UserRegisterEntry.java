package de.reactivejukebox.logger;

import de.reactivejukebox.model.UserPlain;

public class UserRegisterEntry extends Entry {
    public UserRegisterEntry(UserPlain userPlain) {
        super(Event.USER_REGISTER, userPlain);
    }
}
