package de.reactivejukebox.datahandlers;

import de.reactivejukebox.model.*;

import java.sql.SQLException;


/**
 * The TendencyHandler is used manage Tendencies
 */
public class TendencyHandler {

    private Tendencies tendencies;

    public TendencyHandler() {
        tendencies = Model.getInstance().getTendencies();
    }

    /**
     * Adds a Tendency from a User to the Database
     *
     * @throws SQLException if something goes wrong
     */
    public Tendency addTendency(TendencyPlain tendency, User user) throws SQLException {
        tendency.setUserId(user.getId());
        return tendencies.put(tendency);
    }


}
