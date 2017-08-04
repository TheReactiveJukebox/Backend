package de.reactivejukebox.model;


import de.reactivejukebox.database.DatabaseProvider;
import de.reactivejukebox.database.PreparedStatementBuilder;
import de.reactivejukebox.recommendations.strategies.StrategyType;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Radios implements Iterable<Radio> {

    private static final String INSERT_RADIO =
            "INSERT INTO radio (userid, AlgorithmName, StartYear, EndYear) VALUES (?, ?, ?, ?);";
    private  static final String SELECT_RADIO =
            "SELECT * FROM radio WHERE userid = ? ORDER BY id DESC LIMIT 1;";
    private static final String INSERT_REFERENCE_SONG =
            "INSERT INTO radio_song (radioid, songid, position) VALUES (?, ?, ?);";
    private static final String SELECT_REFERENCE_SONG =
            "SELECT SongId FROM radio_song WHERE RadioId = ? ORDER BY Position;";
    private static final String INSERT_GENRE =
            "INSERT INTO radio_genre (RadioId, GenreId) VALUES (?, (SELECT genre.Id FROM genre WHERE genre.name = ?));";
    private static final String SELECT_GENRE =
            "SELECT genre.Name AS GenreName, genre.Id AS GenreId FROM radio JOIN radio_genre ON radio.Id = radio_genre.RadioId JOIN genre ON radio_genre.GenreId = genre.Id WHERE radio.Id = ?;";

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
            radio = build(fromDB(id));
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
        // convert track id array to List<Track>
        List<Track> startTracks = null;
        int[] tracks = radio.getStartTracks();
        if (tracks != null) {
            startTracks = Arrays.stream(radio.getStartTracks())
                    .boxed()
                    .map(i -> Model.getInstance().getTracks().get(i))
                    .collect(Collectors.toList());
        }

        // return rich radio object
        return new Radio(
                radio.getId(),
                users.get(radio.getUserId()),
                radio.getGenres(),
                radio.getMood(),
                radio.getStartYear(),
                radio.getEndYear(),
                startTracks,
                StrategyType.valueOf(radio.getAlgorithm())
        );
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
        PreparedStatement getRadio = con.prepareStatement(SELECT_RADIO);
        getRadio.setInt(1, radio.getUserId());
        ResultSet rs = getRadio.executeQuery();
        if (rs.next()) {
            radio.setId(rs.getInt("id"));
            radio.setUserId(rs.getInt("userid"));
            radio.setAlgorithm(rs.getString("AlgorithmName"));
            radio.setStartYear(rs.getInt("StartYear"));
            radio.setEndYear(rs.getInt("EndYear"));
            // TODO read more radio attributes
            radio.setStartTracks(fromDBReferenceSongs(radio.getId(), con));
            radio.setGenres(fromDBGenres(radio.getId(), con));
            con.close();
            return radio;
        } else {
            con.close();
            throw new SQLException();
        }
    }


    private RadioPlain fromDB(int id) throws SQLException {
        RadioPlain radio = null;
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        stmnt = new PreparedStatementBuilder()
                .select("*")
                .from("radio")
                .addFilter("Id=?", (query, i) -> query.setInt(i, id));
        PreparedStatement dbQuery = stmnt.prepare(con);
        ResultSet rs = dbQuery.executeQuery();
        if (rs.next()) {
            radio = new RadioPlain();
            radio.setId(rs.getInt("id"));
            radio.setUserId(rs.getInt("userid"));
            radio.setAlgorithm(rs.getString("AlgorithmName"));
            radio.setStartYear(rs.getInt("StartYear"));
            radio.setEndYear(rs.getInt("EndYear"));
            // TODO read more radio attributes
            radio.setStartTracks(fromDBReferenceSongs(id, con));
            radio.setGenres(fromDBGenres(id, con));
        }
        con.close();
        return radio;
    }

    private int[] fromDBReferenceSongs(int id, Connection con) throws SQLException {
        PreparedStatement getReferenceSongs = con.prepareStatement(SELECT_REFERENCE_SONG);
        getReferenceSongs.setInt(1, id);
        ResultSet rs = getReferenceSongs.executeQuery();
        List<Integer> list = new LinkedList<>();
        while (rs.next()) {
            list.add(rs.getInt("SongId"));
        }
        int[] result = list.stream()
                .mapToInt(i->i)
                .toArray();
        return result;
    }

    private String[] fromDBGenres(int id, Connection con) throws SQLException {
        PreparedStatement getGenres = con.prepareStatement(SELECT_GENRE);
        getGenres.setInt(1, id);
        ResultSet rs = getGenres.executeQuery();
        ArrayList<String> list = new ArrayList<>();
        while (rs.next()) {
            list.add(rs.getString("GenreName"));
        }
        return list.toArray(new String[list.size()]);
    }

    private void toDB(RadioPlain radio) throws SQLException {
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        // use a transaction
        con.setAutoCommit(false);
        // insert new radio in database
        PreparedStatement addRadio = con.prepareStatement(INSERT_RADIO, Statement.RETURN_GENERATED_KEYS);
        addRadio.setInt(1, radio.getUserId());
        addRadio.setString(2, radio.getAlgorithm());
        if (radio.getStartYear() == 0) {
            addRadio.setNull(3, Types.INTEGER);
        } else {
            addRadio.setInt(3, radio.getStartYear());
        }
        if (radio.getEndYear() == 0) {
            addRadio.setNull(4, Types.INTEGER);
        } else {
            addRadio.setInt(4, radio.getEndYear());
        }
        // TODO ad more radio attributes here
        addRadio.executeUpdate();
        // add new id from database to entry object
        ResultSet rs = addRadio.getGeneratedKeys();
        if (rs.next()) {
            radio.setId(rs.getInt(1));
        }
        // insert reference songs
        int[] referenceSongs = radio.getStartTracks();
        if (referenceSongs != null && referenceSongs.length > 0) {
            PreparedStatement addReferenceSong = con.prepareStatement(INSERT_REFERENCE_SONG);
            for (int i = 0; i < referenceSongs.length;) {
                addReferenceSong.setInt(1, radio.getId());
                addReferenceSong.setInt(2, referenceSongs[i]);
                i++;
                addReferenceSong.setInt(3, i);
                addReferenceSong.addBatch();
            }
            addReferenceSong.executeBatch();
        }
        // insert genres
        String[] genres = radio.getGenres();
        if (genres != null && genres.length > 0) {
            PreparedStatement addGenre = con.prepareStatement(INSERT_GENRE);
            for (String genre: genres) {
                addGenre.setInt(1, radio.getId());
                addGenre.setString(2, genre);
                addGenre.addBatch();
            }
            addGenre.executeBatch();
        }
        // end transaction
        con.commit();

        // TODO check of needs to reset auto commit settings
        con.setAutoCommit(true);
        con.close();
    }
}