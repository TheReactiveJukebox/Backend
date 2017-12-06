package de.reactivejukebox.logger;

import de.reactivejukebox.model.UserPlain;

public class UserLoggedInEntry extends Entry {
    public UserLoggedInEntry(UserPlain user) {
        super(Event.USER_LOGIN, user);
    }

}
