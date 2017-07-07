package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseAccessObject;

import java.sql.SQLException;

public class UserD {
    Integer id;
    String username;
    String password;
    String token;
    String InviteKey;

    public UserD(){}

    public UserD(int id, String username, String token){
        this.id = id;
        this.username=username;
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
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInviteKey() {
        return InviteKey;
    }

    public void setInviteKey(String inviteKey) {
        InviteKey = inviteKey;
    }
    public User getUser(){
        User user;
        try {
            user = DatabaseAccessObject.getInstance().getUsers().get(this.username);
        } catch (SQLException e) {
            user = new User();
            e.printStackTrace();
        }
        return user;
    }
}
