package de.reactivejukebox.user;

import de.reactivejukebox.core.Database;
import org.postgresql.util.PSQLException;

import javax.security.auth.login.FailedLoginException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * The TokenHandler is used to create and manage the login {@link Token}s and the users table in the Database.
 */
public class TokenHandler {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private static TokenHandler instance;
    private HashMap<String, UserData> tokenMap;
    private PreparedStatement updateToken, insertUser, selectByUser, selectByToken;

    /**
     * create first instance of user-token mapping
     * set Database connections Settings
     */
    private TokenHandler() {

        tokenMap = new HashMap<>();
        try {
            /* create connection and prepare statements. Note that the Connection is never closed.
             * This is because the connection is held until the server is shut down.
             */
            Connection db = Database.getInstance().getConnection();
            updateToken = db.prepareStatement("UPDATE jukebox_user SET Token = ? WHERE Name = ?;");
            insertUser = db.prepareStatement("INSERT INTO jukebox_user (Name, Password, Token) VALUES ( ?, ?, ?);");
            selectByUser = db.prepareStatement("SELECT Id, Name, Password FROM jukebox_user WHERE Name = ?;");
            selectByToken = db.prepareStatement("SELECT Id, Name, Password FROM jukebox_user WHERE Token = ?;");
        } catch (SQLException e) {
            throw new RuntimeException("could not establish connection to Database please restart or contact developer!");
        }

    }

    /**
     * Delivers the single {@link TokenHandler} Instance to organize the users.
     */
    public static synchronized TokenHandler getTokenHandler() {
        if (TokenHandler.instance == null) {
            TokenHandler.instance = new TokenHandler();
        }
        return TokenHandler.instance;
    }

    /**
     * Checks whether the {@link Token} exists. Note that this method
     * returns nothing if the Token is fine and throws an error otherwise.
     *
     * @throws SQLException if the token is invalid
     */
    public Token checkToken(Token token) throws SQLException {
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
     * @throws SQLException if there is no user with this token
     */
    public UserData getUser(Token token) throws SQLException {
        UserData user = tokenMap.get(token.getToken());
        if (user == null) {
            // check Database for the user
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
        token.setToken(null);
        updateTokenAtDB(user, token);
    }

    /**
     * adds a new User to the Database and generates a Token
     *
     * @param newUser name and password of the new User
     * @throws SQLException if the user already exist
     */
    public Token register(UserData newUser) throws SQLException {
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
     * @throws SQLException        if the user does not exist
     * @throws FailedLoginException if the user credentials are wrong
     */
    public Token checkUser(UserData user) throws SQLException, FailedLoginException {
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
     * @throws SQLException if there is no user with the specified name
     */
    private UserData getUserFromDBbyName(UserData user) throws SQLException {
        selectByUser.setString(1, user.getUsername());
        return getUserFromDB(selectByUser);
    }

    /**
     * This method queries the Database for the specified user by {@link Token} and composes a {@link UserData}
     * object out of the retrieved information
     *
     * @throws SQLException if there is no user with the specified token
     */
    private UserData getUserFromDBbyToken(Token token) throws SQLException {
        selectByToken.setString(1, token.getToken());
        return getUserFromDB(selectByToken);
    }

    private UserData getUserFromDB(PreparedStatement stmnt) throws SQLException {
        UserData user = new UserData();
        ResultSet rs = stmnt.executeQuery();

        if (rs.next()) {
            user.setUserID(rs.getInt("Id"));
            user.setUsername(rs.getString("Name"));
            user.setHashedPassword(rs.getString("Password"));
            return user;
        } else {
            throw new SQLException();
        }
    }

    /**
     * Inserts the userdata in the users table
     *
     * @throws SQLException if the user already exist
     */
    private void registerUserAtDB(UserData user, Token token) throws SQLException {
        insertUser.setString(1, user.getUsername());
        insertUser.setString(2, user.getPassword());
        insertUser.setString(3, token.getToken());
        insertUser.executeUpdate();
    }

    /**
     * changes the token attribute in the Database
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
