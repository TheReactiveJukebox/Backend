package de.reactivejukebox.user;


import de.reactivejukebox.datahandlers.TokenHandler;
import de.reactivejukebox.model.User;
import de.reactivejukebox.model.UserPlain;
import de.reactivejukebox.model.Users;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;

import javax.security.auth.login.FailedLoginException;
import java.sql.SQLException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Tests all public TokenHandler Methods
 */
public class TokenHandlerTest {

    /**
     * Test to check that the TokenHandler checks Users correctly
     */
    @Test
    public void checkUserTest() {
        try {
            // Arrange
            Users users = mock(Users.class);

            UserPlain userPlain = new UserPlain();
            userPlain.setUsername("Hans Wurst");
            userPlain.setPassword("123456");
            userPlain.setId(42);

            User user = new User();
            user.setUsername("Hans Wurst");
            user.setPassword("123456");
            user.setId(42);

            TokenHandler tokenHandlerUnderTest = new TokenHandler(users);

            when(users.get(any(UserPlain.class))).thenReturn(user);
            when(users.changeToken(any(User.class))).thenReturn(user);

            // Act
            UserPlain result = tokenHandlerUnderTest.checkUser(userPlain);

            // Assert
            assertEquals(userPlain, result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occured in the test: " + e.getMessage());
        }
    }

    /**
     * Test to check that when the users object fails, the TokenHandler fails too
     *
     * @throws Exception The SQLException that is produced
     */
    @Test(expectedExceptions = SQLException.class)
    public void checkUserThrowsSqlExceptionTest() throws Exception {

        // Arrange
        Users users = mock(Users.class);

        UserPlain userPlain = new UserPlain();
        userPlain.setUsername("Hans Wurst");
        userPlain.setPassword("123456");
        userPlain.setId(42);

        TokenHandler tokenHandlerToTest = new TokenHandler(users);

        when(users.get(any(UserPlain.class))).thenThrow(SQLException.class);

        // Act
        UserPlain result = tokenHandlerToTest.checkUser(userPlain);

        // Assert
        assertEquals(userPlain, result);
    }

    /**
     * Test to provoke an FailedLoginException when the password is not correct
     */
    @Test(expectedExceptions = FailedLoginException.class)
    public void checkUserFailsWithWrongPasswordTest() throws Exception {
        // Arrange
        Users users = mock(Users.class);

        UserPlain userPlain = new UserPlain();
        userPlain.setUsername("Hans Wurst");
        userPlain.setPassword("123456");

        User user = new User();
        user.setUsername("Hans Wurst");
        user.setPassword("definitely not 123456");

        when(users.get(any(UserPlain.class))).thenReturn(user);

        TokenHandler tokenHandlerToTest = new TokenHandler(users);

        // Act
        UserPlain result = tokenHandlerToTest.checkUser(userPlain);

        // Assert
        fail("The FailedLoginException was not thrown");
    }

}