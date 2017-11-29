package de.reactivejukebox.logger;

import de.reactivejukebox.model.UserPlain;

public class EntryImp implements Entry {
    private long unixTime;
    private Event ev;
    private UserPlain user;

    public EntryImp(final Event ev, final UserPlain user) {
        this.ev = ev;
        this.user = user;
        unixTime = System.currentTimeMillis() / 1000L;
    }

    @Override
    public Long getTime() {
        return unixTime;
    }

    @Override
    public Event getEvent() {
        return ev;
    }

    @Override
    public Integer getUserId() {
        if (user == null)
            return null;
        return user.getId();
    }

    public boolean isValid() {
        switch (ev) {
            case USER_LOGIN:
                return getUserId() != null;
            default:
                return false;
        }
    }
}
