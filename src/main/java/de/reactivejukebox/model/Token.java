package de.reactivejukebox.model;

import java.io.Serializable;

/**
 * This is a simple Container to Store a authentication token.
 */
public class Token implements Serializable {

    String token;

    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Token [token=" + token + "]";
    }
}