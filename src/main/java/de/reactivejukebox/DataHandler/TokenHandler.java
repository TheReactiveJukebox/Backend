package de.reactivejukebox.DataHandler;

import de.reactivejukebox.database.DatabaseAccessObject;
import de.reactivejukebox.model.User;
import de.reactivejukebox.model.UserD;
import de.reactivejukebox.model.Users;

import javax.security.auth.login.FailedLoginException;
import java.sql.*;


/**
 * The TokenHandler is used to create and manage the login {@link Token}s and the users table in the Database.
 */
public class TokenHandler {
    private Users users;

    public TokenHandler(){
        users = DatabaseAccessObject.getInstance().getUsers();
    }
    /**
     * Checks the login credentials of a user and generates a valid token. In short: the login is performed.
     *
     * @param user the retrieved login credentials
     * @return a new valid {@link Token} for the user
     * @throws SQLException        if the user does not exist
     * @throws FailedLoginException if the user credentials are wrong
     */
    public UserD checkUser(UserD user) throws SQLException, FailedLoginException {
        //compare password and username with database
        User dbUser = users.get(user);
        if (!dbUser.getUsername().equals(user.getUsername())
                || !dbUser.getPassword().equals(user.getPassword())) {
            throw new FailedLoginException("Wrong Username or Password");
        }
        dbUser = users.changeToken(dbUser);
        return dbUser.getUserD();
    }

    /**
     * Checks whether the {@link Token} exists. Note that this method
     * returns nothing if the Token is fine and throws an error otherwise.
     *
     * @throws SQLException if the token is invalid
     */
    public UserD checkToken(UserD token) throws SQLException {
        return users.getByToken(token.getToken()).getUserD();
    }

    /**
     * removes the token from the local HashMap and the Database
     *
     * @param token to remove
     */
    public void logout(UserD token) throws SQLException {
        User user = users.getByToken(token.getToken());
        users.changeToken(user);
    }

    /**
     * adds a new User to the Database and generates a Token
     *
     * @param newUser name and password of the new User
     * @throws SQLException if the user already exist
     */
    public UserD register(User newUser) throws SQLException {
        //generate Token and try to register
        //if there are any conflicts, the database will throw an exception
        return users.add(newUser).getUserD();
    }
}
