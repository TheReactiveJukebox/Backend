package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseFactory;
import de.reactivejukebox.database.PreparedStatementBuilder;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class HistoryEntries {
    protected Connection con;
    protected PreparedStatementBuilder stmnt;
    protected ArrayList<HistoryEntry> entryList;
    private ConcurrentHashMap<Integer, HistoryEntry> entryById;
    private Users users;

    public HistoryEntries(Users users) {
        entryById = new ConcurrentHashMap<>();
        this.users = users;
    }

    public HistoryEntry addEntry(HistoryEntryPlain entry) throws SQLException {
        if (entry.getTime() == null) {
            LocalDateTime lt = LocalDateTime.now(ZoneId.of("UTC"));
            Timestamp t = Timestamp.valueOf(lt);
            t.setNanos(0);
            entry.setTime(t);
        }
        toDB(entry);
        HistoryEntry newEntry = build(fromDB(entry));
        entryById.putIfAbsent(newEntry.getId(), newEntry);
        return newEntry;
    }

    public HistoryEntry get(int id) throws SQLException {
        HistoryEntry entry;
        if (entryById.containsKey(id)) {
            entry = entryById.get(id);
        } else {
            entry = build(fromDB("Id", id).get(0));
        }
        return entry;
    }

    public HistoryEntry get(HistoryEntryPlain entry) throws SQLException {
        HistoryEntry newEntry;
        if (entry.getId() != null) {
            newEntry = get(entry.getId());
        } else if (entry.getTime() != null) {
            newEntry = build(fromDB(entry));
            entryById.putIfAbsent(newEntry.getId(), newEntry);
        } else {
            throw new SQLException();
        }
        return newEntry;
    }

    public ArrayList<HistoryEntry> getListbyUserId(int id) throws SQLException {
        return build(fromDB("UserId", id));
    }

    private HistoryEntry build(HistoryEntryPlain entry) throws SQLException {
        //TODO get real Objects
        Track t = new Track();
        t.setId(entry.getTrackId());
        Radio r = new Radio();
        r.setId(entry.getRadioId());
        User u = users.get(entry.getUserId());
        return new HistoryEntry(entry.getId(), t, r, u, entry.getTime());
    }

    private ArrayList<HistoryEntry> build(ArrayList<HistoryEntryPlain> entries) throws SQLException {
        ArrayList<HistoryEntry> newList = new ArrayList<>();
        Iterator<HistoryEntryPlain> iterator = entries.listIterator();
        while (iterator.hasNext()) {
            newList.add(build(iterator.next()));
        }
        return newList;
    }

    private HistoryEntryPlain fromDB(HistoryEntryPlain entry) throws SQLException {

        con = DatabaseFactory.getInstance().getDatabase().getConnection();
        stmnt = new PreparedStatementBuilder();
        stmnt.select("*");
        stmnt.from("history");
        stmnt.addFilter("SongId = '" + entry.getTrackId() + "'");
        stmnt.addFilter("RadioId = '" + entry.getRadioId() + "'");
        stmnt.addFilter("UserId = '" + entry.getUserId() + "'");
        stmnt.addFilter("Time = '" + entry.getTime() + "'");
        return fromDB().get(0);
    }

    private ArrayList<HistoryEntryPlain> fromDB(String col, Object o) throws SQLException {
        con = DatabaseFactory.getInstance().getDatabase().getConnection();
        stmnt = new PreparedStatementBuilder();
        stmnt.select("*");
        stmnt.from("history");
        stmnt.addFilter(col + " = '" + o.toString() + "'");
        return fromDB();
    }


    private ArrayList<HistoryEntryPlain> fromDB() throws SQLException {
        PreparedStatement dbQuery = stmnt.prepare(con);
        ArrayList<HistoryEntryPlain> results = new ArrayList<>();
        ResultSet rs = dbQuery.executeQuery();
        while (rs.next()) {
            HistoryEntryPlain entry = new HistoryEntryPlain();
            entry.setId(rs.getInt("id"));
            entry.setTrackId(rs.getInt("songId"));
            entry.setRadioId(rs.getInt("radioId"));
            entry.setUserId(rs.getInt("userId"));
            entry.setTime(rs.getTimestamp("time"));
            results.add(entry);
        }
        con.close();
        return results;
    }

    private void toDB(HistoryEntryPlain entry) throws SQLException {
        con = DatabaseFactory.getInstance().getDatabase().getConnection();
        PreparedStatement addEntry = con.prepareStatement("INSERT INTO history (songId, userId, radioId,time) VALUES ( ?, ?, ?,?);");
        addEntry.setInt(1, entry.getTrackId());
        addEntry.setInt(2, entry.getUserId());
        addEntry.setInt(3, entry.getRadioId());
        addEntry.setTimestamp(4,entry.getTime());
        addEntry.executeUpdate();
        con.close();
    }


}
