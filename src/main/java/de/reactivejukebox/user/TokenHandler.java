package de.reactivejukebox.user;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;

/**
 * Author: Thilo Kamradt
 */
public class TokenHandler {
    private static TokenHandler instance;
    private static String dbAdress = "db:5432";
    private HashMap<String, UserData> tokenMap;
    private Connection db;
    private Statement st;
    private ResultSet rs;

    /**
     * Delivers the single {@link TokenHandler} Instance to organize the users.
     */
    public static TokenHandler getTokenHandler() {
        if (TokenHandler.instance == null) {
            TokenHandler.instance = new TokenHandler();
        }
        return TokenHandler.instance;
    }

    /**
     * create first instance of user-token mapping
     * set Database connections Settings
     */
    private TokenHandler() {

        tokenMap = new HashMap<>();
        // triger loading the JDBC Driver
        /* this is bad style,it would be much better to start the appliation with
         * 'java -Djdbc.drivers=org.postgresql.Driver example.ImageViewer
         */
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
        }

    }

    /**
     * Checks whether the {@link Token} exists. Note that this method
     * returns nothing if the Token is fine and throws an error elsewise.
     */
    public void checkToken(Token token) throws InvalidKeyException {
        if (!tokenMap.containsKey(token.getToken())) {
            UserData dbuser = this.getUserFromDBForToken(token);
            tokenMap.put(token.getToken(), dbuser);
        }
    }

    /**
     * Get the {@link UserData} which is connected to a specific {@link Token}
     *
     * @param token the {@link Token} of the user you want
     * @return the matching {@link UserData} to the {@link Token}
     */
    public UserData getUser(Token token) throws InvalidKeyException {
        UserData user = tokenMap.get(token.getToken());
        if (user == null) {
            //maybe it is not really useful to check the database here
            user = this.getUserFromDBForToken(token);
            tokenMap.put(token.getToken(), user);
        }
        return user;
    }

    /**
     * adds a new User to the Database and generates a Token
     * @param newUser name and password of the new User
     */
    public Token register(UserData newUser) throws Exception {
        UserData dbuser = this.getUserFromDB(newUser);
        if (dbuser.getUsername() != null) {
            throw new Exception("User already exists");
        }
        newUser.setHashedPassword(newUser.getPassword());
        Token nextToken = generateToken(newUser);
        this.registerUserAtDB(newUser, nextToken);

        //add the user including the generated uid to local HashMap
        tokenMap.put(nextToken.getToken(), this.getUserFromDB(newUser));
        return nextToken;
    }

    public Token CheckUser(UserData user) throws Exception {
        //check password and username
        user.setHashedPassword(user.getPassword());
        UserData dbUser = getUserFromDB(user);
        if (dbUser.getUsername() == null || !dbUser.getUsername().equals(user.getUsername())
                || !dbUser.getPassword().equals(user.getPassword())) {
            throw new Exception("Wrong Username or Password");
        }

        //generate Token and update Database
        Token token = generateToken(user);
        this.updateTokenAtDB(user, token);
        this.tokenMap.put(token.getToken(), user);
        return token;
    }

    private Token generateToken(UserData user) {
        return new Token(System.currentTimeMillis() + user.getUsername());
    }

    private UserData getUserFromDB(UserData user) {
        UserData dbUser = new UserData();

        try {
            Connection db = DriverManager.getConnection("jdbc:postgresql://" + dbAdress + "/reactivejukebox", "backend", "xxx");
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("SELECT uid, username, password FROM users WHERE username=\'" + user.getUsername() + "\'");

            if (rs.next()) {
                //directly fill UserData because there can only be one row since usernames are unique
                dbUser.setUserID(Integer.valueOf(rs.getNString("uid")));
                dbUser.setUsername(rs.getNString("username"));
                dbUser.setHashedPassword(rs.getNString("password"));
            }

            rs.close();
            st.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dbUser;
    }

    private UserData getUserFromDBForToken(Token token) throws InvalidKeyException {
        UserData dbUser = new UserData();

        try {
            Connection db = DriverManager.getConnection("jdbc:postgresql://" + dbAdress + "/reactivejukebox", "backend", "xxx");
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("SELECT uid, username, password FROM users WHERE token=\'" + token.getToken() + "\'");


            if (rs.next()) {
                dbUser.setUserID(Integer.valueOf(rs.getNString("uid")));
                dbUser.setUsername(rs.getNString("username"));
                dbUser.setHashedPassword(rs.getNString("password"));
            }
            if (rs.next()) {
                //multiple useres are using this Token
                throw new InvalidKeyException("Token is not unique");
            }

            rs.close();
            st.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dbUser;
    }

    private void registerUserAtDB(UserData user, Token token) {
        try {
            Connection db = DriverManager.getConnection("jdbc:postgresql://" + dbAdress + "/reactivejukebox", "backend", "xxx");
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery("INSERT INTO users (username, password, token,) VALUES (" + user.getUsername() + ", " + user.getPassword() + ", " + token.getToken() + ");");
            rs.close();
            st.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTokenAtDB(UserData user, Token token) {
        try {
            Connection db = DriverManager.getConnection("jdbc:postgresql://" + dbAdress + "/reactivejukebox", "backend", "xxx");
            Statement st = db.createStatement();
            ResultSet rs = st.executeQuery(" UPDATE users SET token="+token.getToken()+" WHERE username="+user.getUsername()+";");
            rs.close();
            st.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
