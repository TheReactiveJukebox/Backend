package de.reactivejukebox.user;

import java.io.Serializable;
import java.util.List;

public class UserData implements Serializable {
    String username;
    String password;
    int id;
    List<String> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "Auth [username=" + username + ", id=" + id + "]";
    }
}