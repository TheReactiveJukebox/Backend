package de.reactivejukebox.user;


import de.reactivejukebox.core.TokenHandler;
import de.reactivejukebox.model.User;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.security.auth.login.FailedLoginException;
import java.sql.SQLException;

/**
 * Tests all public TokenHandler Methods
 */
public class TokenHandlerTest {
    User testUser;
    Token testToken;

    /**
     * Register testUser for the tests
     */
    @BeforeClass
    public void Setup() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("drfvzrf4!!e785cw4x***'##+'§$%$rjgzu43worjk3");
        try {
            testToken = TokenHandler.getTokenHandler().register(testUser);
        } catch (SQLException e) {
            try {
                testToken = TokenHandler.getTokenHandler().checkUser(testUser);
            } catch (SQLException e1) {
                Assert.fail();
            } catch (FailedLoginException e1) {
                Assert.fail();
            }
        }
        Assert.assertNotEquals(testToken.getToken(), null);
        System.out.print(testToken.getToken() + "\n");
        System.out.print(testUser.getUsername());
    }

    /**
     * test getUser for an registered User
     */
    @Test
    public void testGetUser() {
        User checkUser = null;
        try {
            checkUser = TokenHandler.getTokenHandler().getUser(testToken);
        } catch (SQLException e) {
            Assert.fail();
        }
        Assert.assertEquals(checkUser.getUsername(), testUser.getUsername());
    }

    /**
     * test getUser for an non registered User
     */
    @Test(expectedExceptions = SQLException.class)
    public void testGetWrongUser() throws SQLException {
        Token wrongToken = new Token();
        wrongToken.setToken("test123456");
        TokenHandler.getTokenHandler().getUser(wrongToken);
    }

    /**
     * test register with new username (only works one time)
     */
    @Test(dependsOnMethods = {"testGetUser", "testGetWrongUser"})
    public void testRegister() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setUsername("newPW");
        User checkUser = null;
        Token newToken = null;
        try {
            newToken = TokenHandler.getTokenHandler().register(newUser);
        } catch (SQLException e) {
            Assert.fail("User already exists, possibly from earlier tests");
        }
        try {
            checkUser = TokenHandler.getTokenHandler().getUser(newToken);
        } catch (SQLException e) {
            Assert.fail();
        }
        Assert.assertEquals(checkUser.getUsername(), newUser.getUsername());
    }

    /**
     * test register with an already used username
     */
    @Test(expectedExceptions = SQLException.class)
    public void testWrongRegister() throws SQLException {
        TokenHandler.getTokenHandler().register(testUser);
    }
    /**
     * test checkToken with token for a registered user
     */
    @Test(dependsOnMethods = {"testGetUser"})
    public void testCheckToken() {
        Token newToken = null;
        try {
            newToken = TokenHandler.getTokenHandler().checkToken(testToken);
        } catch (SQLException e) {
            Assert.fail();
        }
        User checkUser = null;
        try {
            checkUser = TokenHandler.getTokenHandler().getUser(newToken);
        } catch (SQLException e) {
            Assert.fail();
        }
        Assert.assertEquals(checkUser.getUsername(), testUser.getUsername());
    }

    /**
     * test checkToken with a wrong token
     * @throws SQLException
     */
    @Test(expectedExceptions = SQLException.class)
    public void testCheckWrongToken() throws SQLException {
        Token wrongToken = new Token();
        wrongToken.setToken("test123456");
        TokenHandler.getTokenHandler().checkToken(wrongToken);
    }

    /**
     * test check User with a registered user
     */
    @Test(dependsOnMethods = {"testGetUser", "testGetWrongUser"})
    public void testCheckUser() {
        User checkUser = new User();
        checkUser.setPassword("drfvzrf4!!e785cw4x***'##+'§$%$rjgzu43worjk3");
        checkUser.setUsername("testUser");
        Token checkToken = new Token();
        try {
            checkToken = TokenHandler.getTokenHandler().checkUser(checkUser);
        } catch (SQLException e) {
            Assert.fail();
        } catch (FailedLoginException e) {
            Assert.fail();
        }
        User checkUser2 = null;
        try {
            checkUser2 = TokenHandler.getTokenHandler().getUser(checkToken);
        } catch (SQLException e) {
            Assert.fail();
        }
        Assert.assertEquals(checkUser2.getUsername(), testUser.getUsername());

    }

    /**
     * test checkUser with non registered user
     * @throws SQLException
     * @throws FailedLoginException
     */
    @Test(expectedExceptions = {SQLException.class, FailedLoginException.class})
    public void testCheckWrongUser() throws SQLException, FailedLoginException {
        User wrongUser = new User();
        wrongUser.setUsername("Blubb");
        wrongUser.setPassword("drfvzrf4!!e785cw4x***'##+'§$%$rjgzu43worjk3");
        TokenHandler.getTokenHandler().checkUser(wrongUser);
    }

    /**
     * test checkUser with a wrong password
     * @throws SQLException
     * @throws FailedLoginException
     */
    @Test(expectedExceptions = {SQLException.class, FailedLoginException.class})
    public void testCHeckWrongPWUser() throws SQLException, FailedLoginException {
        User wrongUser = new User();
        wrongUser.setUsername("testUser");
        wrongUser.setPassword("wrongPW");
        TokenHandler.getTokenHandler().checkUser(wrongUser);
    }

    /**
     * test logout
     */
    @Test(dependsOnMethods = {"testCheckUser"})
    public void testLogout() {
        TokenHandler.getTokenHandler().logout(testToken);
        try {
            User test = TokenHandler.getTokenHandler().getUser(testToken);
            Assert.fail(test.toString());
        } catch (SQLException e) {
        }
        try {
            testToken = TokenHandler.getTokenHandler().checkUser(testUser);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FailedLoginException e) {
            e.printStackTrace();
        }
        Assert.assertNotEquals(testToken.getToken(), null);

    }

    /**
     * print username / Token for comparison
     */
    @AfterClass
    public void result() {
        System.out.print("Changed User / Token\n");
        System.out.print(testToken.getToken() + "\n");
        System.out.print(testUser.getUsername());
    }
}