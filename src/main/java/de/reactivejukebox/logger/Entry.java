package de.reactivejukebox.logger;

public interface Entry {
    Long getTime();

    Event getEvent();

    Integer getUserId();

    //Integer getSongId();
    boolean isValid();
}
