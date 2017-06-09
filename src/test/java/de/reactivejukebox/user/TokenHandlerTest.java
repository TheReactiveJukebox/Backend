package de.reactivejukebox.user;

/**
 * Created by lang on 6/9/17.
 */

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.security.auth.login.FailedLoginException;
import java.sql.SQLException;

public class TokenHandlerTest {
    UserData testUser;
    Token testToken;

    @BeforeClass
    public void Setup() {
        testUser = new UserData();
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

    @Test
    public void testGetUser() {
        UserData checkUser = null;
        try {
            checkUser = TokenHandler.getTokenHandler().getUser(testToken);
        } catch (SQLException e) {
            Assert.fail();
        }
        Assert.assertEquals(checkUser.getUsername(), testUser.getUsername());
    }

    @Test(expectedExceptions = SQLException.class)
    public void testGetWrongUser() throws SQLException {
        Token wrongToken = new Token();
        wrongToken.setToken("test123456");
        TokenHandler.getTokenHandler().getUser(wrongToken);
    }

    @Test(dependsOnMethods = {"testGetUser", "testGetWrongUser"})
    public void testRegister() {
        UserData newUser = new UserData();
        newUser.setUsername("newUser");
        newUser.setUsername("newPW");
        UserData checkUser = null;
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

    @Test(expectedExceptions = SQLException.class)
    public void testWrongRegister() throws SQLException {
        TokenHandler.getTokenHandler().register(testUser);
    }

    @Test(dependsOnMethods = {"testGetUser"})
    public void testCheckToken() {
        Token newToken = null;
        try {
            newToken = TokenHandler.getTokenHandler().checkToken(testToken);
        } catch (SQLException e) {
            Assert.fail();
        }
        UserData checkUser = null;
        try {
            checkUser = TokenHandler.getTokenHandler().getUser(newToken);
        } catch (SQLException e) {
            Assert.fail();
        }
        Assert.assertEquals(checkUser.getUsername(), testUser.getUsername());
    }

    @Test(expectedExceptions = SQLException.class)
    public void testCheckWrongToken() throws SQLException {
        Token wrongToken = new Token();
        wrongToken.setToken("test123456");
        TokenHandler.getTokenHandler().checkToken(wrongToken);
    }

    @Test(dependsOnMethods = {"testGetUser", "testGetWrongUser"})
    public void testCheckUser() {
        UserData checkUser = new UserData();
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
        UserData checkUser2 = null;
        try {
            checkUser2 = TokenHandler.getTokenHandler().getUser(checkToken);
        } catch (SQLException e) {
            Assert.fail();
        }
        Assert.assertEquals(checkUser2.getUsername(), testUser.getUsername());

    }

    @Test(expectedExceptions = {SQLException.class, FailedLoginException.class})
    public void testCheckWrongUser() throws SQLException, FailedLoginException {
        UserData wrongUser = new UserData();
        wrongUser.setUsername("Blubb");
        wrongUser.setPassword("drfvzrf4!!e785cw4x***'##+'§$%$rjgzu43worjk3");
        TokenHandler.getTokenHandler().checkUser(wrongUser);
    }

    @Test(expectedExceptions = {SQLException.class, FailedLoginException.class})
    public void testCHeckWrongPWUser() throws SQLException, FailedLoginException {
        UserData wrongUser = new UserData();
        wrongUser.setUsername("testUser");
        wrongUser.setPassword("wrongPW");
        TokenHandler.getTokenHandler().checkUser(wrongUser);
    }

    @Test(dependsOnMethods = {"testCheckUser"})
    public void testLogout() {
        TokenHandler.getTokenHandler().logout(testToken);
        try {
            UserData test = TokenHandler.getTokenHandler().getUser(testToken);
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

    @AfterClass
    public void result() {
        System.out.print("Changed UserData / Token\n");
        System.out.print(testToken.getToken() + "\n");
        System.out.print(testUser.getUsername());
    }


    // @Test(expectedExceptions = ArithmeticException.class)
    // @Test(dependsOnMethods = { "initEnvironmentTest" })
}