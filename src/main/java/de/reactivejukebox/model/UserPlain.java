package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseAccessObject;

import java.sql.SQLException;

public class UserPlain {
    Integer id;
    String username;
    String pwHash;
    String token;
    String inviteKey;

    public UserPlain() {
    }

    public UserPlain(int id, String username, String token) {
        this.id = id;
        this.username = username;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return pwHash;
    }

    public void setPassword(String password) {
        pwHash = User.generateSHA256(password);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInviteKey() {
        return inviteKey;
    }

    public void setInviteKey(String inviteKey) {
        inviteKey = inviteKey;
    }
}
