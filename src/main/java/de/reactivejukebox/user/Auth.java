package de.reactivejukebox.user;

public class Auth {
    String username;
    String password;


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Auth [username=" + username + ", password=" + password + "]";
    }
}