package de.reactivejukebox.model;

import de.reactivejukebox.database.DatabaseProvider;

import java.sql.*;
import java.util.HashMap;


//TODO why are the methods of this class static?
public class IndirectFeedbackEntries {
    private final String INSERT_INDIRECT_FEEDBACK = "INSERT INTO indirectFeedback (SongId, UserId, RadioId, Type, Position, ToSongId) VALUES (?, ?, ?, ?, ?, ?);";

    public IndirectFeedbackPlain put(IndirectFeedbackPlain entry) throws SQLException {
        toDB(entry);
        return entry;
    }

    public HashMap<Integer, Integer> getSkipFeedback(int radioId, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM indirectFeedback WHERE userid = ? " +
                "radioId = ? Type = SKIP");
        getFeedback.setInt(1, userId);
        getFeedback.setInt(2, radioId);
        ResultSet rs = getFeedback.executeQuery();
        HashMap<Integer, Integer> result = new HashMap<>();

        while (rs.next()) {
            int count = result.getOrDefault(rs.getInt("songId"),0);
            count += 1;
            result.put(rs.getInt("songId"), count);
        }
        con.close();
        return result;
    }

    public HashMap<Integer, Integer> getDeleteFeedback(int radioId, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM indirectFeedback WHERE userid = ? " +
                "radioId = ? Type = DELETE");
        getFeedback.setInt(1, userId);
        getFeedback.setInt(2, radioId);
        ResultSet rs = getFeedback.executeQuery();
        HashMap<Integer, Integer> result = new HashMap<>();

        while (rs.next()) {
            int count = result.getOrDefault(rs.getInt("songId"),0);
            count += 1;
            result.put(rs.getInt("songId"), count);
        }
        con.close();
        return result;
    }

    public HashMap<Integer, Integer> getMultiSkipFeedback(int radioId, int userId) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM indirectFeedback WHERE userid = ? " +
                "radioId = ? Type = MULTI_SKIP");
        getFeedback.setInt(1, userId);
        getFeedback.setInt(2, radioId);
        ResultSet rs = getFeedback.executeQuery();
        HashMap<Integer, Integer> result = new HashMap<>();

        while (rs.next()) {
            int count = result.getOrDefault(rs.getInt("songId"),0);
            count += 1;
            result.put(rs.getInt("songId"), count);
        }
        con.close();
        return result;
    }




    private void toDB(IndirectFeedbackPlain entry) throws SQLException {
        Connection con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addEntry = con.prepareStatement(INSERT_INDIRECT_FEEDBACK, Statement.RETURN_GENERATED_KEYS);
        addEntry.setInt(1, entry.getTrackId());
        addEntry.setInt(2, entry.getUserId());
        addEntry.setInt(3, entry.getRadioId());
        addEntry.setString(4, entry.getFeedbackName());
        switch (IndirectFeedbackName.valueOf(entry.getFeedbackName())) {
            case SKIP:
                addEntry.setInt(5, entry.getPosition());
                if (entry.getToTrackId() > 0) {
                    addEntry.setInt(6, entry.getToTrackId());
                } else {
                    addEntry.setNull(6, Types.INTEGER); // to Track
                }
                break;
            case MULTI_SKIP:
                addEntry.setInt(5, entry.getPosition());
                addEntry.setInt(6, entry.getToTrackId());
                break;
            case DELETE:
                addEntry.setNull(5, Types.INTEGER); // Position
                addEntry.setNull(6, Types.INTEGER); // to Track
                break;
        }
        addEntry.executeUpdate();
        // add new id from database to entry object
        ResultSet rs = addEntry.getGeneratedKeys();
        if (rs.next()) {
            entry.setId(rs.getInt(1));
        }
        con.close();
    }
}