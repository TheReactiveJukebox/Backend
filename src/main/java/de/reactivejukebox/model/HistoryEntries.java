package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.database.PreparedStatementBuilder;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


//TODO don't keep the entire History in RAM
public class HistoryEntries implements Iterable<HistoryEntry> {
    protected Connection con;
    protected PreparedStatementBuilder stmnt;
    private ConcurrentHashMap<Integer, HistoryEntry> entryById;
    private Users users;
    private Tracks tracks;
    private Radios radios;

    public HistoryEntries(Users users, Tracks tracks, Radios radios) {
        entryById = new ConcurrentHashMap<>();
        this.users = users;
        this.tracks = tracks;
        this.radios = radios;
    }

    public HistoryEntry put(HistoryEntryPlain entry) throws SQLException {
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
            entryById.putIfAbsent(entry.getId(), entry);
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

    public ArrayList<HistoryEntry> getListbyRadioId(int id) throws SQLException {
        return build(fromDB("RadioId", id));
    }

    @Override
    public Iterator<HistoryEntry> iterator() {
        return entryById.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super HistoryEntry> consumer) {
        entryById.values().forEach(consumer);
    }

    @Override
    public Spliterator<HistoryEntry> spliterator() {
        return entryById.values().spliterator();
    }

    public Stream<HistoryEntry> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Build a HistoryEntry object from a HistoryEntryPlain object.
     *
     */
    private HistoryEntry build(HistoryEntryPlain entry) throws SQLException {
        Track t = tracks.get(entry.getTrackId());
        Radio r = radios.get(entry.getRadioId());
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

        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        stmnt = new PreparedStatementBuilder()
                .select("*")
                .from("history")
                .addFilter("SongId=?", (query, i) -> query.setInt(i, entry.getTrackId()))
                .addFilter("RadioId=?", (query, i) -> query.setInt(i, entry.getRadioId()))
                .addFilter("UserId=?", (query, i) -> query.setInt(i, entry.getUserId()))
                .addFilter("Time=?", (query, i) -> query.setTimestamp(i, entry.getTime()));
        return fromDB().get(0);
    }

    private ArrayList<HistoryEntryPlain> fromDB(String col, int id) throws SQLException {
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        stmnt = new PreparedStatementBuilder()
                .select("*")
                .from("history")
                .addFilter(col + "=?", (query, i) -> query.setInt(i, id));
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

    /**
     * Insert HistoryEntryPlain in database table history and set historyId in history object.
     *
     */
    private void toDB(HistoryEntryPlain entry) throws SQLException {
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addEntry = con.prepareStatement("INSERT INTO history (songId, userId, radioId, time) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
        addEntry.setInt(1, entry.getTrackId());
        addEntry.setInt(2, entry.getUserId());
        addEntry.setInt(3, entry.getRadioId());
        addEntry.setTimestamp(4, entry.getTime());
        addEntry.executeUpdate();
        // add new id from database to entry object
        ResultSet rs = addEntry.getGeneratedKeys();
        if (rs.next()) {
            entry.setId(rs.getInt(1));
        }
        con.close();
    }


}
