package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseFactory;
import de.reactivejukebox.database.PreparedStatementBuilder;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

public class Users {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    protected PreparedStatementBuilder stmnt;
    protected Connection con;
    private ConcurrentHashMap<String, User> userByName;
    private ConcurrentHashMap<Integer, User> userById;
    private ConcurrentHashMap<String, User> userByToken;

    public Users() {
        userByName = new ConcurrentHashMap<>();
        userById = new ConcurrentHashMap<>();
        userByToken = new ConcurrentHashMap<>();
    }

    public void add(User user) throws SQLException {
        toDB(user);
        user = fromDB("name", user.getUsername());
        user.setToken(generateToken(user));
        userByName.put(user.getUsername(), user);
        userById.put(user.getId(), user);
        userByToken.put(user.getToken(), user);

    }

    public User get(int id) throws SQLException {
        User user;
        if (userById.containsKey(id)) {
            user = userById.get(id);
        } else {
            user = fromDB("id", id);
        }
        return user;
    }

    public User get(String name) throws SQLException {
        User user;
        if (userByToken.containsKey(name)) {
            user = userByToken.get(name);
        } else {
            user = fromDB("name", name);
        }
        return user;
    }

    public User getByToken(String token) throws SQLException {
        User user;
        if (userByToken.containsKey(token)) {
            user = userByToken.get(token);
        } else {
            user = fromDB("token", token);
        }
        return user;
    }

    public void changeToken(User user) throws SQLException {
        user = get(user.getUsername());
        String oldT = user.getToken();
        user.setToken(generateToken(user));
        if(userByToken.containsKey(oldT)){
            userByToken.remove(oldT);
        }
        userByToken.put(user.getToken(),user);

    }

    private User fromDB(String col, Object o) throws SQLException {
        con = DatabaseFactory.getInstance().getDatabase().getConnection();
        stmnt = new PreparedStatementBuilder();
        stmnt.select("*");
        stmnt.from("jukebox_user");
        stmnt.addFilter(col + " = '" + o.toString() + "'");
        stmnt.prepare(con);
        PreparedStatement dbQuery = stmnt.prepare(con);

        User user = new User();
        ResultSet rs = dbQuery.executeQuery();
        if (rs.next()) {
            user.setUserID(rs.getInt("id"));
            user.setUsername(rs.getString("name"));
            user.setHashedPassword(rs.getString("password"));
            user.setToken((rs.getString("token")));
            con.close();
            return user;
        } else {
            con.close();
            throw new SQLException();
        }

    }

    private void toDB(User user) throws SQLException {
        con = DatabaseFactory.getInstance().getDatabase().getConnection();
        PreparedStatement addUser = con.prepareStatement("INSERT INTO jukebox_user (name, password, token) VALUES ( ?, ?, ?);");
        addUser.setString(1, user.getUsername());
        addUser.setString(2, user.getPassword());
        addUser.setString(3, user.getToken());
        addUser.executeUpdate();
        con.close();
    }

    private String generateToken(User user) throws SQLException {
        String t = sdf.format(new Timestamp(System.currentTimeMillis())) + user.getUsername().substring(0, 2);
        con = DatabaseFactory.getInstance().getDatabase().getConnection();
        PreparedStatement updateToken = con.prepareStatement("UPDATE jukebox_user SET Token = ? WHERE id = ?;");
        updateToken.setString(1, user.getToken());
        updateToken.setInt(2, user.getId());
        updateToken.executeUpdate();
        con.close();
        return t;
    }

}
