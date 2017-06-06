package de.reactivejukebox.user;

public class UserData {
    String username;
    String password;
    int id;


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {return id; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(int id)  {
        this.id= id;
    }

    @Override
    public String toString() {
        return "Auth [username=" + username + ", id=" + id + "]";
    }
}