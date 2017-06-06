package de.reactivejukebox.user;

import java.io.Serializable;

public class Token implements Serializable {

    String token;

    public  Token(String token){
        this.token = token;
    }

    public  Token(){
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