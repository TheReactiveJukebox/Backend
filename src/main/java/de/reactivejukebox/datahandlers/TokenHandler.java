package de.reactivejukebox.datahandlers;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.User;
import de.reactivejukebox.model.UserPlain;
import de.reactivejukebox.model.Users;

import javax.security.auth.login.FailedLoginException;
import java.sql.*;


/**
 * The TokenHandler is used to create and manage the login {@link }s and the users table in the Database.
 */
public class TokenHandler {
    private Users users;

    public TokenHandler(){
        users = Model.getInstance().getUsers();
    }
    /**
     * Checks the login credentials of a user and generates a valid token. In short: the login is performed.
     *
     * @param user the retrieved login credentials
     * @return a new valid {@link } for the user
     * @throws SQLException        if the user does not exist
     * @throws FailedLoginException if the user credentials are wrong
     */
    public UserPlain checkUser(UserPlain user) throws SQLException, FailedLoginException {
        //compare password and username with database
        User dbUser = users.get(user);
        if (!dbUser.getUsername().equals(user.getUsername())
                || !dbUser.getPassword().equals(user.getPassword())) {
            throw new FailedLoginException("Wrong Username or Password");
        }
        dbUser = users.changeToken(dbUser);
        return dbUser.getPlainObject();
    }

    /**
     * Checks whether the {@link } exists. Note that this method
     * returns nothing if the Token is fine and throws an error otherwise.
     *
     * @throws SQLException if the token is invalid
     */
    public UserPlain checkToken(UserPlain token) throws SQLException {
        return users.getByToken(token.getToken()).getPlainObject();
    }

    /**
     * removes the token from the local HashMap and the Database
     *
     * @param token to remove
     */
    public void logout(UserPlain token) throws SQLException {
        User user = users.getByToken(token.getToken());
        users.changeToken(user);
    }

    /**
     * adds a new User to the Database and generates a Token
     *
     * @param newUser name and password of the new User
     * @throws SQLException if the user already exist
     */
    public UserPlain register(UserPlain newUser) throws SQLException {
        //generate Token and try to register
        //if there are any conflicts, the database will throw an exception
        return users.put(newUser).getPlainObject();
    }
}
