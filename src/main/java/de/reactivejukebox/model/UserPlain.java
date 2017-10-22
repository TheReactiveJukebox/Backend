package de.reactivejukebox.model;

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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;
        UserPlain o = (UserPlain) other;
        return this.id.equals(o.id)
                && this.username.equals(o.username);
    }

    @Override
    public String toString() {
        return String.format("UserPlan(id=%d, username=%s)", id, username);
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
        this.inviteKey = inviteKey;
    }
}
