package de.reactivejukebox.user;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This is a simple class to store the retrieved JSON-Login Credentials
 * Authors: Andreas Lang, Thilo Kamradt
 */
public class UserData implements Serializable {
    protected String username;
    protected String password;
    protected String pwHash;
    protected int userID;


    public UserData() {
        //TODO remove after testing (if working, the passowrd kann directly be hashed)
        try {
            System.out.println("----------------- name:" + username + " password:" + password);
        } catch (Exception e) {
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        if (pwHash == null) {
            if(password == null) {
                return null;
            }
            pwHash = generateSHA256(password);
            password = null;
        }
        return pwHash;
    }

    public int getUserID() {
        return userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        pwHash = this.generateSHA256(password);
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setHashedPassword(String hashedPassword) {
        this.pwHash = hashedPassword;
    }

    private String generateSHA256(String message) {
        String hashedMessage = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
            hashedMessage = hash.toString();
        } catch (NoSuchAlgorithmException e) {
            //will never happen
            System.err.println("SHA-256 is not available anymore");
        }
        return hashedMessage;
    }

    @Override
    public String toString() {
        return "UserData [username=" + username + ", userID=" + userID + "]";
    }
}