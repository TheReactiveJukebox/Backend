package de.reactivejukebox.logger;

import de.reactivejukebox.model.UserPlain;

public class UserLoggedOutEntry extends Entry {
    public UserLoggedOutEntry(UserPlain userPlain) {
        super(Event.USER_LOGOUT, userPlain);
    }
}
