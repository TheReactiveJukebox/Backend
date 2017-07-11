package de.reactivejukebox.model;


import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.database.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Radios implements Iterable<Radio> {
    protected Users users;
    protected PreparedStatementBuilder stmnt;
    protected Connection con;
    protected ConcurrentHashMap<Integer, Radio> radioById;
    protected ConcurrentHashMap<Integer, Radio> radioByUserId;

    public Radios(Users users) {
        radioById = new ConcurrentHashMap<>();
        radioByUserId = new ConcurrentHashMap<>();
        this.users = users;
    }

    public Radio put(RadioPlain radio) throws SQLException {
        toDB(radio);
        radio = fromDB(radio);
        Radio newRadio = build(radio);
        radioById.putIfAbsent(newRadio.getId(), newRadio);
        radioByUserId.remove(radio.getUserId());
        radioByUserId.putIfAbsent(newRadio.getUser().getId(), newRadio);
        return newRadio;
    }

    public Radio get(int id) throws SQLException {
        Radio radio;
        if (radioById.containsKey(id)) {
            radio = radioById.get(id);
        } else {
            radio = build(fromDB(id).get(0));
            radioById.putIfAbsent(radio.getId(), radio);
            radioByUserId.putIfAbsent(radio.getUser().getId(), radio);
        }
        return radio;
    }

    public Radio getByUserId(int id) throws SQLException {
        Radio radio;
        if (radioByUserId.containsKey(id)) {
            radio = radioByUserId.get(id);
        } else {
            RadioPlain dummy = new RadioPlain();
            dummy.setUserId(id);
            radio = build(fromDB(dummy));
            radioById.putIfAbsent(radio.getId(), radio);
            radioByUserId.putIfAbsent(radio.getUser().getId(), radio);
        }
        return radio;
    }

    public Radio get(RadioPlain radio) throws SQLException {
        return get(radio.getId());
    }


    @Override
    public Iterator<Radio> iterator() {
        return radioById.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super Radio> consumer) {
        radioById.values().forEach(consumer);
    }

    @Override
    public Spliterator<Radio> spliterator() {
        return radioById.values().spliterator();
    }

    public Stream<Radio> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    private Radio build(RadioPlain radio) throws SQLException {
        Radio newRadio = new Radio();
        newRadio.setRandom(radio.isRandom());
        newRadio.setUser(users.get(radio.getUserId()));
        newRadio.setId(radio.getId());
        newRadio.setEndYear(radio.getEndYear());
        newRadio.setGenres(radio.getGenres());
        newRadio.setStartYear(radio.getStartYear());
        newRadio.setMood(radio.getMood());
        return newRadio;
    }

    private ArrayList<Radio> build(ArrayList<RadioPlain> radioList) throws SQLException {
        ArrayList<Radio> newList = new ArrayList<>();
        Iterator<RadioPlain> iterator = radioList.listIterator();
        while (iterator.hasNext()) {
            newList.add(build(iterator.next()));
        }
        return newList;
    }

    private RadioPlain fromDB(RadioPlain radio) throws SQLException {
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getRadio = con.prepareStatement("SELECT * FROM radio WHERE userid = ? ORDER BY id DESC LIMIT 1;");
        getRadio.setInt(1, radio.getUserId());
        ResultSet rs = getRadio.executeQuery();
        if (rs.next()) {
            radio.setId(rs.getInt("id"));
            radio.setUserId(rs.getInt("userid"));
            radio.setRandom(rs.getBoolean("israndom"));
            con.close();
            return radio;
        } else {
            con.close();
            throw new SQLException();
        }
    }


    private ArrayList<RadioPlain> fromDB(int id) throws SQLException {
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        stmnt = new PreparedStatementBuilder()
                .select("*")
                .from("radio")
                .addFilter("Id=?", (query, i) -> query.setInt(i, id));
        PreparedStatement dbQuery = stmnt.prepare(con);
        ArrayList<RadioPlain> results = new ArrayList<>();
        ResultSet rs = dbQuery.executeQuery();
        while (rs.next()) {
            RadioPlain radio = new RadioPlain();
            radio.setId(rs.getInt("id"));
            radio.setUserId(rs.getInt("userid"));
            radio.setRandom(rs.getBoolean("israndom"));
            results.add(radio);
        }
        con.close();
        return results;
    }

    private void toDB(RadioPlain radio) throws SQLException {
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addUser = con.prepareStatement("INSERT INTO radio (userid, israndom) VALUES (?, ?);");
        addUser.setInt(1, radio.getUserId());
        addUser.setBoolean(2, radio.isRandom());
        addUser.executeUpdate();
        con.close();
    }
}
