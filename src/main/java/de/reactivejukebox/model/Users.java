package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.database.PreparedStatementBuilder;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Users implements Iterable<User> {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private ConcurrentHashMap<String, User> userByName;
    private ConcurrentHashMap<Integer, User> userById;
    private ConcurrentHashMap<String, User> userByToken;

    public Users() {
        userByName = new ConcurrentHashMap<>();
        userById = new ConcurrentHashMap<>();
        userByToken = new ConcurrentHashMap<>();
    }

    public User put(UserPlain user) throws SQLException {
        toDB(user);
        PreparedStatementBuilder builder = new PreparedStatementBuilder()
                .select("*")
                .from("jukebox_user")
                .addFilter("name=?", (query, i) -> query.setString(i, user.getUsername()));
        User newUser = createUserFromStatement(builder);
        generateToken(newUser);
        userByName.put(newUser.getUsername(), newUser);
        userById.put(newUser.getId(), newUser);
        userByToken.put(newUser.getToken(), newUser);
        return newUser;
    }

    public User get(int id) throws SQLException {
        User user;
        if (userById.containsKey(id)) {
            user = userById.get(id);
        } else {
            PreparedStatementBuilder builder = new PreparedStatementBuilder()
                    .select("*")
                    .from("jukebox_user")
                    .addFilter("id=?", (query, i) -> query.setInt(i, id));
            user = createUserFromStatement(builder);
            userById.putIfAbsent(user.getId(),user);
        }
        return user;
    }

    public User get(String name) throws SQLException {
        User user;
        if (userByName.containsKey(name)) {
            user = userByName.get(name);
        } else {
            PreparedStatementBuilder builder = new PreparedStatementBuilder()
                    .select("*")
                    .from("jukebox_user")
                    .addFilter("name=?", (query, i) -> query.setString(i, name));
            user = createUserFromStatement(builder);
            userByName.putIfAbsent(user.getUsername(),user);
        }
        return user;
    }

    public User getByToken(String token) throws SQLException {
        User user;
        if (userByToken.containsKey(token)) {
            user = userByToken.get(token);
        } else {
            PreparedStatementBuilder builder = new PreparedStatementBuilder()
                    .select("*")
                    .from("jukebox_user")
                    .addFilter("token=?", (query, i) -> query.setString(i, token));
            user = createUserFromStatement(builder);
        }
        return user;
    }

    public User changeToken(User user) throws SQLException {
        user = get(user.getUsername());
        String oldT = user.getToken();
        generateToken(user);
        if( oldT != null && userByToken.containsKey(oldT)) {
            userByToken.remove(oldT);
        }
        userByToken.put(user.getToken(), user);
        return user;
    }

    public User get(UserPlain userD) throws SQLException {
        return get(userD.username);
    }

    @Override
    public Iterator<User> iterator() {
        return userById.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super User> consumer) {
        userById.values().forEach(consumer);
    }

    @Override
    public Spliterator<User> spliterator() {
        return userById.values().spliterator();
    }

    public Stream<User> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    private User createUserFromStatement(PreparedStatementBuilder builder) throws SQLException {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet res = null;
        try {
            con = DatabaseProvider.getInstance().getDatabase().getConnection();
            statement = builder.prepare(con);
            res = statement.executeQuery();
            User user = new User();
            if (res.next()) {
                user.setUserID(res.getInt("id"));
                user.setUsername(res.getString("name"));
                user.setHashedPassword(res.getString("password"));
                user.setToken((res.getString("token")));
                return user;
            } else throw new SQLException();
        } finally {
            if (con != null) con.close();
            if (res != null) res.close();
            if (statement != null) statement.close();
        }
    }

    private void toDB(UserPlain user) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addUser = con.prepareStatement("INSERT INTO jukebox_user (name, password, token) VALUES (?, ?, ?);");
        addUser.setString(1, user.getUsername());
        addUser.setString(2, user.getPassword());
        addUser.setString(3, user.getToken());
        addUser.executeUpdate();
        con.close();
    }

    private void generateToken(User user) throws SQLException {
        String t = sdf.format(new Timestamp(System.currentTimeMillis())) + user.getUsername().substring(0, 2);
        user.setToken(t);
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement updateToken = con.prepareStatement("UPDATE jukebox_user SET token = ? WHERE id = ?;");
        updateToken.setString(1, user.getToken());
        updateToken.setInt(2, user.getId());
        updateToken.executeUpdate();
        con.close();
    }

}
