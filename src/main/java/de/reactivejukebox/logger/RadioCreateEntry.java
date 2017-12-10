package de.reactivejukebox.logger;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.User;

public class RadioCreateEntry extends Entry {
    public RadioCreateEntry(User user, Radio radio) {
        super(Event.RADIO_START, user);
        // TODO set radio values
    }
}
