package de.reactivejukebox.user;

/**
 * Created by lang on 6/6/17.
 */
public class TokenHandler {

    public UserData getUser(Token token){

        return new UserData();
    }

    public static TokenHandler getTokenHandler() {

        return new TokenHandler();
    }
}
