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

/**
 * Tendencies is a class containing all the given tendencies. It handles all actions concerning adding,
 * receiving or removing tendencies.
 */
public class Tendencies implements Iterable<Tendency> {
    protected Users users;
    protected Radios radios;
    protected PreparedStatementBuilder stmnt;
    protected Connection con;
    protected ConcurrentHashMap<Integer, Tendency> tendencyById;
    protected ConcurrentHashMap<Integer, ArrayList<Tendency>> tendenciesByUserId;
    protected ConcurrentHashMap<Integer, ArrayList<Tendency>> tendenciesByRadioId;

    public Tendencies(Users users, Radios radios) {
        tendencyById = new ConcurrentHashMap<>();
        tendenciesByUserId = new ConcurrentHashMap<>();
        tendenciesByRadioId = new ConcurrentHashMap<>();
        this.users = users;
        this.radios = radios;
    }

    /**
     * Adds the given tendency to the database and hash maps if it is absent
     * and returns the tendency as Tendency object.
     *
     * @param tendency
     * @return
     * @throws SQLException
     */
    public Tendency put(TendencyPlain tendency) throws SQLException {
        toDB(tendency);
        tendency = fromDbByTendency(tendency);
        Tendency newTendency = build(tendency);
        tendencyById.putIfAbsent(newTendency.getId(), newTendency);
        this.putIfAbsent(tendenciesByUserId, newTendency.getUser().getId(), newTendency);
        this.putIfAbsent(tendenciesByRadioId, newTendency.getRadio().getId(), newTendency);
        return newTendency;
    }

    /**
     * Puts a tendency into a hashmap containing an id as keys and an ArrayList of tendencies as values. If the ArrayList
     * for the given id does not exist, it will be created otherwise tendency will be inserted in this list.
     *
     * @param hashMap  the hashmap to put tendency into
     * @param id       the key for the ArrayList to put tendency into
     * @param tendency the tendency to put into
     */
    private void putIfAbsent(ConcurrentHashMap<Integer, ArrayList<Tendency>> hashMap, Integer id, Tendency tendency) {
        ArrayList<Tendency> tmpList;
        if (hashMap.containsKey(id)) {
            hashMap.get(id).add(tendency);
        } else {
            tmpList = new ArrayList<>();
            tmpList.add(tendency);
            hashMap.put(id, tmpList);
        }

    }

    public Tendency get(int id) throws SQLException {
        Tendency tendency;
        if (tendencyById.containsKey(id)) {
            tendency = tendencyById.get(id);
        } else {
            tendency = build(fromDB(id));
            tendencyById.putIfAbsent(tendency.getId(), tendency);
            this.putIfAbsent(tendenciesByUserId, tendency.getUser().getId(), tendency);
            this.putIfAbsent(tendenciesByRadioId, tendency.getRadio().getId(), tendency);
        }
        return tendency;
    }

    public ArrayList<Tendency> getByUserId(int id) throws SQLException {
        Tendency tmpTendency;
        ArrayList<Tendency> tendencies;
        ArrayList<TendencyPlain> tendenciesPlain;
        if (tendenciesByUserId.containsKey(id)) {
            tendencies = tendenciesByUserId.get(id);
        } else {
            tendenciesPlain = this.fromDbByUserId(id);
            tendencies = new ArrayList<>();
            for (TendencyPlain t : tendenciesPlain) {
                tmpTendency = this.build(t);
                tendencies.add(tmpTendency);
                tendencyById.putIfAbsent(tmpTendency.getId(), tmpTendency);
                this.putIfAbsent(tendenciesByUserId, tmpTendency.getUser().getId(), tmpTendency);
                this.putIfAbsent(tendenciesByRadioId, tmpTendency.getRadio().getId(), tmpTendency);
            }
        }
        return tendencies;
    }

    public Tendency get(TendencyPlain tendency) throws SQLException {
        return get(tendency.getId());
    }


    @Override
    public Iterator<Tendency> iterator() {
        return tendencyById.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super Tendency> consumer) {
        tendencyById.values().forEach(consumer);
    }

    @Override
    public Spliterator<Tendency> spliterator() {
        return tendencyById.values().spliterator();
    }

    public Stream<Tendency> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    private Tendency build(TendencyPlain tendency) throws SQLException {
        Tendency newTendency = new Tendency();

        newTendency.setRadio(radios.get(tendency.getRadioId()));
        newTendency.setUser(users.get(tendency.getUserId()));
        newTendency.setId(tendency.getId());
        newTendency.setFaster(tendency.isFaster());
        newTendency.setSlower(tendency.isSlower());
        newTendency.setNewer(tendency.isNewer());
        newTendency.setOlder(tendency.isOlder());
        newTendency.setLessDynamics(tendency.isLessDynamics());
        newTendency.setMoreDynamics(tendency.isMoreDynamics());
        newTendency.setMoreOfGenre(tendency.getMoreOfGenre());

        return newTendency;
    }


    /**
     * Creates an Arraylist of Tendencies for a given List of TendencyPlains. Attributes in the returned list will be
     * the same, as in the given list
     *
     * @param tendencyList the list of TendencyPlains which shall be converted to Tendencies
     * @return the matching Tendencies for the tendencyList
     * @throws SQLException
     */
    private ArrayList<Tendency> build(ArrayList<TendencyPlain> tendencyList) throws SQLException {
        ArrayList<Tendency> newList = new ArrayList<>();
        Iterator<TendencyPlain> iterator = tendencyList.listIterator();
        while (iterator.hasNext()) {
            newList.add(build(iterator.next()));
        }
        return newList;
    }

    private ArrayList<TendencyPlain> fromDbByUserId(int id) throws SQLException {
        ArrayList<TendencyPlain> tendencies = new ArrayList<>();
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM tendency WHERE userid = ? ORDER BY id DESC;");
        getFeedback.setInt(1, id);
        ResultSet rs = getFeedback.executeQuery();
        while (rs.next()) {
            tendencies.add(this.buildPlain(rs));
        }
        con.close();
        return tendencies;
    }

    private TendencyPlain fromDbByFeedback(TendencyPlain feedback) throws SQLException {
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM tendency WHERE userid = ? " +
                "AND radioid = ?  ORDER BY id DESC;");
        getFeedback.setInt(1, feedback.getUserId());
        getFeedback.setInt(2, feedback.getRadioId());
        ResultSet rs = getFeedback.executeQuery();
        if (rs.next()) {
            TendencyPlain result = this.buildPlain(rs);
            con.close();
            return result;
        } else {
            con.close();
            throw new SQLException("Tendency was not found");
        }

    }

    private TendencyPlain buildPlain(ResultSet rs) throws SQLException {
        TendencyPlain tendency = new TendencyPlain();
        tendency.setId(rs.getInt("id"));
        tendency.setUserId(rs.getInt("userid"));
        tendency.setRadioId(rs.getInt("radioid"));
        //TODO: What does the table look like?

        return tendency;
    }


    private ArrayList<TendencyPlain> fromDbByRadioId(int id) throws SQLException {
        ArrayList<TendencyPlain> tendencies = new ArrayList<>();
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM tendency WHERE radioid = ? ORDER BY id DESC;");
        getFeedback.setInt(1, id);
        ResultSet rs = getFeedback.executeQuery();
        while (rs.next()) {
            tendencies.add(this.buildPlain(rs));
        }
        con.close();
        return tendencies;
    }


    private TendencyPlain fromDB(int id) throws SQLException {
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        stmnt = new PreparedStatementBuilder()
                .select("*")
                .from("tendency")
                .addFilter("Id=?", (query, i) -> query.setInt(i, id));
        PreparedStatement dbQuery = stmnt.prepare(con);
        ResultSet rs = dbQuery.executeQuery();
        if (rs.next()) {
            con.close();
            return (this.buildPlain(rs));
        } else {
            con.close();
            throw new SQLException("Tendency with ID=" + id + " was not found");
        }

    }


    private void toDB(TendencyPlain tendency) throws SQLException {

        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedback (userid, radioid," +
                " ) " +
                "VALUES( ?, ?) ");

        addFeedback.setInt(1, tendency.getUserId());
        addFeedback.setInt(2, tendency.getRadioId());
        //TODO: What does the table look like?

        addFeedback.executeUpdate();
        con.close();

    }
}
