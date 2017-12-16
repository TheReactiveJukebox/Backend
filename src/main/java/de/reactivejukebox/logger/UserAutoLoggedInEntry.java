package de.reactivejukebox.logger;

import de.reactivejukebox.model.UserPlain;

public class UserAutoLoggedInEntry extends Entry {
    public UserAutoLoggedInEntry(UserPlain user) {
        super(Event.USER_AUTOLOGIN, user);
    }
}
