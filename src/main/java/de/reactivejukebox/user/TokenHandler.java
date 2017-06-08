package de.reactivejukebox.user;

import org.postgresql.util.PSQLException;

import javax.security.auth.login.FailedLoginException;
import java.sql.*;
import java.util.HashMap;
import java.text.SimpleDateFormat;

/**
 * The TokenHandler is used to create and manage the login {@link Token}s and the users table at the Database.
 */
public class TokenHandler {
    private static TokenHandler instance;
    private static final String dbAdress = "jdbc:postgresql://localhost:5432/reactivejukebox";
    private static final String dbLoginUser = "backend";
    private static final String dbLoginPassword = "xxx";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private HashMap<String, UserData> tokenMap;
    private PreparedStatement updateToken, insertUser, selectByUser, selectByToken;

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
        // trigger loading the JDBC Driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            //will not happen since the driver is a working dependency
        }
        try {
            /* create connection and prepare statements. Note that the Connection is never closed.
             * This is because the connection is held until the server is shut down.
             */
            Connection db = DriverManager.getConnection(dbAdress, dbLoginUser, dbLoginPassword);
            updateToken = db.prepareStatement(" UPDATE users SET token = ? WHERE username = ?;");
            insertUser = db.prepareStatement("INSERT INTO users (username, pw, token) VALUES ( ?, ?, ?);");
            selectByUser = db.prepareStatement("SELECT uid, username, pw FROM users WHERE username = ?;");
            selectByToken = db.prepareStatement("SELECT uid, username, pw FROM users WHERE token = ?;");
        } catch (SQLException e) {
            throw new RuntimeException("could not establish connection to Database please restart or contact developer!");
        }

    }

    /**
     * Checks whether the {@link Token} exists. Note that this method
     * returns nothing if the Token is fine and throws an error elsewise.
     *
     * @throws PSQLException if the token is invalid
     */
    public Token checkToken(Token token) throws PSQLException {
        if (!tokenMap.containsKey(token.getToken())) {
            UserData dbuser = this.getUserFromDBbyToken(token);
            tokenMap.put(token.getToken(), dbuser);
        }
        return token;
    }

    /**
     * Get the {@link UserData} which is connected to a specific {@link Token}
     *
     * @param token the {@link Token} of the user you want
     * @return the matching {@link UserData} to the {@link Token}
     * @throws PSQLException if there is no user with this token
     */
    public UserData getUser(Token token) throws PSQLException {
        UserData user = tokenMap.get(token.getToken());
        if (user == null) {
            //TODO remove this afte
            //maybe it is not really useful to check the database here
            user = this.getUserFromDBbyToken(token);
            tokenMap.put(token.getToken(), user);
        }
        return user;
    }

    /**
     * removes the token from the local HashMap and the Database
     *
     * @param token to remove
     */
    public void logout(Token token) {
        UserData user = tokenMap.get(token.getToken());
        tokenMap.remove(token.getToken());
        token.setToken("");
        updateTokenAtDB(user, token);
    }

    /**
     * adds a new User to the Database and generates a Token
     *
     * @param newUser name and password of the new User
     * @throws PSQLException if the user already exist
     */
    public Token register(UserData newUser) throws PSQLException {
        //generate Token and try to register
        //if there are any conflicts, the database will throw an exception
        Token nextToken = generateToken(newUser);
        this.registerUserAtDB(newUser, nextToken);

        //add the user including the generated uid to local HashMap
        tokenMap.put(nextToken.getToken(), this.getUserFromDBbyName(newUser));
        return nextToken;
    }

    /**
     * Checks the login credentials of a user and generates a valid token. In short: the login is performed.
     *
     * @param user the retrieved login credentials
     * @return a new valid {@link Token} for the user
     * @throws PSQLException        if the user does not exist
     * @throws FailedLoginException if the user credentials are wrong
     */
    public Token checkUser(UserData user) throws PSQLException, FailedLoginException {
        //compare password and username with database
        UserData dbUser = getUserFromDBbyName(user);
        if (!dbUser.getUsername().equals(user.getUsername())
                || !dbUser.getPassword().equals(user.getPassword())) {
            throw new FailedLoginException("Wrong Username or Password");
        }

        //generate Token and update Database
        Token token = generateToken(dbUser);
        this.updateTokenAtDB(dbUser, token);
        this.tokenMap.put(token.getToken(), dbUser);
        return token;
    }

    /**
     * Generates a {@link Token} build of a Timestamp and the username with a total length of 17 characters.
     *
     * @param user the connected user
     * @return the new valid {@link Token}
     */
    private Token generateToken(UserData user) {
        // if token size grow then adapt database table users
        return new Token(sdf.format(new Timestamp(System.currentTimeMillis())) + user.getUsername().substring(0, 2));
    }

    /**
     * This method queries the Database for the specified user by username and composes a {@link UserData}
     * object out of the retrieved information
     *
     * @throws PSQLException if there is no user with the specified name
     */
    private UserData getUserFromDBbyName(UserData user) throws PSQLException {
        UserData dbUser = new UserData();

        try {
            selectByUser.setString(1, user.getUsername());
            ResultSet rs = selectByUser.executeQuery();

            if (rs.next()) {
                //directly fill UserData because there can only be one row since usernames are unique
                dbUser.setUserID(rs.getInt("uid"));
                dbUser.setUsername(rs.getString("username"));
                dbUser.setHashedPassword(rs.getString("pw"));
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dbUser;
    }

    /**
     * This method queries the Database for the specified user by {@link Token} and composes a {@link UserData}
     * object out of the retrieved information
     *
     * @throws PSQLException if there is no user with the specified token
     */
    private UserData getUserFromDBbyToken(Token token) throws PSQLException {
        UserData userAtDB = new UserData();

        try {
            selectByToken.setString(1, token.getToken());
            ResultSet rs = selectByToken.executeQuery();

            if (rs.next()) {
                userAtDB.setUserID(rs.getInt("uid"));
                userAtDB.setUsername(rs.getString("username"));
                userAtDB.setHashedPassword(rs.getString("pw"));
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userAtDB;
    }

    /**
     * Inserts the userdata at the users table
     *
     * @throws PSQLException if the user already exist
     */
    private void registerUserAtDB(UserData user, Token token) throws PSQLException {
        try {
            insertUser.setString(1, user.getUsername());
            insertUser.setString(2, user.getPassword());
            insertUser.setString(3, token.getToken());
            insertUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * changes the token attribute at the Database
     */
    private void updateTokenAtDB(UserData user, Token token) {
        try {
            updateToken.setString(1, token.getToken());
            updateToken.setString(2, user.getUsername());
            updateToken.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
