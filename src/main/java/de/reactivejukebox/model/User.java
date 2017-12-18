package de.reactivejukebox.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * This is a simple class to store the retrieved JSON-Login credentials and hash the password.
 **/
public class User implements Serializable {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    protected String username;
    protected String password;
    protected String pwHash;
    protected String inviteKey;
    protected Integer userID;
    protected String token;
    protected List<String> roles;

    protected static String generateSHA256(String message) {
        String hashedMessage = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));

            hashedMessage = User.bytesToHexString(hash).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            //will never happen
            System.err.println("SHA-256 is not available anymore");
            e.printStackTrace();
        }
        return hashedMessage;
    }

    private static String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Method the recieve the password. Note that this is not the real password but the SHA-256 hash of it.
     */
    public String getPassword() {
        if (pwHash == null) {
            if (password == null) {
                return null;
            }
            pwHash = generateSHA256(password);
            password = null;
        }
        return pwHash;
    }

    public void setPassword(String password) {
        pwHash = this.generateSHA256(password);
    }

    public String getInviteKey() {
        return inviteKey;
    }

    public void setInviteKey(String inviteKey) {
        this.inviteKey = inviteKey;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setHashedPassword(String hashedPassword) {
        this.pwHash = hashedPassword;
    }

    public int getId() {
        return userID;
    }

    public void setId(int id) {
        this.userID = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User [username=" + username + ", userID=" + userID + "]";
    }

    public UserPlain getPlainObject() {
        return new UserPlain(userID, username, token);
    }
}