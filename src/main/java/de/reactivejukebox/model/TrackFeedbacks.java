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
 * TrackFeedbacks is a class containing all the given track feedbacks. It handles all actions concerning adding,
 * receiving or removing track feedback.
 */
public class TrackFeedbacks implements Iterable<TrackFeedback> {
    protected Users users;
    protected Radios radios;
    protected Tracks tracks;
    protected PreparedStatementBuilder stmnt;
    protected Connection con;
    protected ConcurrentHashMap<Integer, TrackFeedback> feedbackById;
    protected ConcurrentHashMap<Integer, ArrayList<TrackFeedback>> feedbacksByUserId;
    protected ConcurrentHashMap<Integer, ArrayList<TrackFeedback>> feedbacksByRadioId;

    public TrackFeedbacks(Users users, Tracks tracks, Radios radios) {
        feedbackById = new ConcurrentHashMap<>();
        feedbacksByUserId = new ConcurrentHashMap<>();
        feedbacksByRadioId = new ConcurrentHashMap<>();
        this.users = users;
        this.radios = radios;
        this.tracks = tracks;
    }

    /**
     * Adds the given feedback to the database or updates a feedback with the same userid, songid and radioid as the
     * given feedback, adds the feedback to the hash map if it is absent and returns the feedback as TrackFeedback object.
     *
     * @param feedback
     * @return
     * @throws SQLException
     */
    public TrackFeedback put(TrackFeedbackPlain feedback) throws SQLException {
        toDB(feedback);
        feedback = fromDbByFeedback(feedback);
        TrackFeedback newTrackFeedback = build(feedback);

        feedbackById.putIfAbsent(newTrackFeedback.getId(), newTrackFeedback);
        this.putIfAbsent(feedbacksByUserId, newTrackFeedback.getUser().getId(), newTrackFeedback);
        this.putIfAbsent(feedbacksByRadioId, newTrackFeedback.getRadio().getId(), newTrackFeedback);
        return newTrackFeedback;
    }

    /**
     * Puts a trackFeedback into a hashmap containing an id as keys and an ArrayList of trackFeedbacks as values. If the ArrayList
     * for the given id does not exist, it will be created otherwise trackFeedback will be inserted in this list.
     *
     * @param hashMap       the hashmap to put trackFeedback into
     * @param id            the key for the ArrayList to put trackFeedback into
     * @param trackFeedback the track feedback to put into
     */
    private void putIfAbsent(ConcurrentHashMap<Integer, ArrayList<TrackFeedback>> hashMap, Integer id, TrackFeedback trackFeedback) {
        ArrayList<TrackFeedback> tmpList;
        if (hashMap.containsKey(id)) {
            hashMap.get(id).add(trackFeedback);
        } else {
            tmpList = new ArrayList<>();
            tmpList.add(trackFeedback);
            hashMap.put(id, tmpList);
        }

    }

    public TrackFeedback get(int id) throws SQLException {
        TrackFeedback trackFeedback;
        if (feedbackById.containsKey(id)) {
            trackFeedback = feedbackById.get(id);
        } else {
            trackFeedback = build(fromDB(id));
            feedbackById.putIfAbsent(trackFeedback.getId(), trackFeedback);
            this.putIfAbsent(feedbacksByUserId, trackFeedback.getUser().getId(), trackFeedback);
            this.putIfAbsent(feedbacksByRadioId, trackFeedback.getRadio().getId(), trackFeedback);
        }
        return trackFeedback;
    }

    public ArrayList<TrackFeedback> getByUserId(int id) throws SQLException {
        TrackFeedback tmpTrackFeedback;
        ArrayList<TrackFeedback> trackFeedbacks;
        ArrayList<TrackFeedbackPlain> feedbacksPlain;
        if (feedbacksByUserId.containsKey(id)) {
            trackFeedbacks = feedbacksByUserId.get(id);
        } else {
            feedbacksPlain = this.fromDbByUserId(id);
            trackFeedbacks = new ArrayList<>();
            for (TrackFeedbackPlain f : feedbacksPlain) {
                tmpTrackFeedback = this.build(f);
                trackFeedbacks.add(tmpTrackFeedback);
                feedbackById.putIfAbsent(tmpTrackFeedback.getId(), tmpTrackFeedback);
                this.putIfAbsent(feedbacksByUserId, tmpTrackFeedback.getUser().getId(), tmpTrackFeedback);
                this.putIfAbsent(feedbacksByRadioId, tmpTrackFeedback.getRadio().getId(), tmpTrackFeedback);
            }
        }
        return trackFeedbacks;
    }

    public TrackFeedback get(TrackFeedbackPlain feedback) throws SQLException {
        return get(feedback.getId());
    }


    @Override
    public Iterator<TrackFeedback> iterator() {
        return feedbackById.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super TrackFeedback> consumer) {
        feedbackById.values().forEach(consumer);
    }

    @Override
    public Spliterator<TrackFeedback> spliterator() {
        return feedbackById.values().spliterator();
    }

    public Stream<TrackFeedback> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    private TrackFeedback build(TrackFeedbackPlain feedback) throws SQLException {
        TrackFeedback newTrackFeedback = new TrackFeedback();

        newTrackFeedback.setRadio(radios.get(feedback.getRadioId()));
        newTrackFeedback.setUser(users.get(feedback.getUserId()));
        newTrackFeedback.setId(feedback.getId());
        newTrackFeedback.setTrack(tracks.get(feedback.getTrackId()));

        newTrackFeedback.setSongFeedback(convertToInt(feedback.isSongLiked(), feedback.isSongDisliked()));
        newTrackFeedback.setArtistFeedback(convertToInt(feedback.isArtistLiked(), feedback.isArtistDisliked()));
        newTrackFeedback.setSpeedFeedback(convertToInt(feedback.isSpeedLiked(), feedback.isSpeedDisliked()));
        newTrackFeedback.setGenreFeedback(convertToInt(feedback.isGenreLiked(), feedback.isGenreDisliked()));
        newTrackFeedback.setDynamicsFeedback(convertToInt(feedback.isDynamicsLiked(), feedback.isDynamicsDisliked()));
        newTrackFeedback.setPeriodFeedback(convertToInt(feedback.isPeriodLiked(), feedback.isPeriodDisliked()));
        newTrackFeedback.setMoodFeedback(convertToInt(feedback.isMoodLiked(), feedback.isMoodDisliked()));

        return newTrackFeedback;
    }

    private int convertToInt(boolean liked, boolean notLiked) {
        if (liked) return 1;
        if (notLiked) return -1;
        return 0;
    }

    /**
     * Creates an Arraylist of TrackFeedbacks for a given List of TrackFeedbackPlains. Attributes in the returned list will be
     * the same, as in the given list
     *
     * @param feedbackList the list of TrackFeedbackPlains which shall be converted to TrackFeedbacks
     * @return the matching TrackFeedbacks for the feedbackList
     * @throws SQLException
     */
    private ArrayList<TrackFeedback> build(ArrayList<TrackFeedbackPlain> feedbackList) throws SQLException {
        ArrayList<TrackFeedback> newList = new ArrayList<>();
        Iterator<TrackFeedbackPlain> iterator = feedbackList.listIterator();
        while (iterator.hasNext()) {
            newList.add(build(iterator.next()));
        }
        return newList;
    }

    private ArrayList<TrackFeedbackPlain> fromDbByUserId(int id) throws SQLException {
        ArrayList<TrackFeedbackPlain> feedbacks = new ArrayList<>();
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedback WHERE userid = ? ORDER BY id DESC;");
        getFeedback.setInt(1, id);
        ResultSet rs = getFeedback.executeQuery();
        while (rs.next()) {
            feedbacks.add(this.buildPlain(rs));
        }
        con.close();
        return feedbacks;
    }

    private TrackFeedbackPlain fromDbByFeedback(TrackFeedbackPlain feedback) throws SQLException {
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedback WHERE userid = ? " +
                "AND radioid = ?  AND songid = ? ORDER BY id DESC;");
        getFeedback.setInt(1, feedback.getUserId());
        getFeedback.setInt(2, feedback.getRadioId());
        getFeedback.setInt(3, feedback.getTrackId());
        ResultSet rs = getFeedback.executeQuery();
        if (rs.next()) {
            TrackFeedbackPlain result = this.buildPlain(rs);
            con.close();
            return result;
        } else {
            con.close();
            throw new SQLException("TrackFeedback was not found");
        }

    }

    private TrackFeedbackPlain buildPlain(ResultSet rs) throws SQLException {
        TrackFeedbackPlain feedback = new TrackFeedbackPlain();
        feedback.setId(rs.getInt("id"));
        feedback.setUserId(rs.getInt("userid"));
        feedback.setRadioId(rs.getInt("radioid"));
        feedback.setTrackId(rs.getInt("songid"));

        feedback.setSongLiked(rs.getInt("feedbacksong") > 0);
        feedback.setSongDisliked(rs.getInt("feedbacksong")< 0);

        feedback.setArtistLiked(rs.getInt("feedbackartist") > 0);
        feedback.setArtistDisliked(rs.getInt("feedbackartist")< 0);

        feedback.setSpeedLiked(rs.getInt("feedbackspeed") > 0);
        feedback.setSpeedDisliked(rs.getInt("feedbackspeed")< 0);

        feedback.setGenreLiked(rs.getInt("feedbackgenre") > 0);
        feedback.setGenreDisliked(rs.getInt("feedbackgenre")< 0);

        feedback.setDynamicsLiked(rs.getInt("feedbackdynamics") > 0);
        feedback.setDynamicsDisliked(rs.getInt("feedbackdynamics")< 0);

        feedback.setPeriodLiked(rs.getInt("feedbackperiod") > 0);
        feedback.setPeriodDisliked(rs.getInt("feedbackperiod")< 0);

        feedback.setMoodLiked(rs.getInt("feedbackmood") > 0);
        feedback.setMoodDisliked(rs.getInt("feedbackmood")< 0);

        return feedback;
    }


    private ArrayList<TrackFeedbackPlain> fromDbByRadioId(int id) throws SQLException {
        ArrayList<TrackFeedbackPlain> feedbacks = new ArrayList<>();
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement getFeedback = con.prepareStatement("SELECT * FROM feedback WHERE radioid = ? ORDER BY id DESC;");
        getFeedback.setInt(1, id);
        ResultSet rs = getFeedback.executeQuery();
        while (rs.next()) {
            feedbacks.add(this.buildPlain(rs));
        }
        con.close();
        return feedbacks;
    }


    private TrackFeedbackPlain fromDB(int id) throws SQLException {
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        stmnt = new PreparedStatementBuilder()
                .select("*")
                .from("feedback")
                .addFilter("Id=?", (query, i) -> query.setInt(i, id));
        PreparedStatement dbQuery = stmnt.prepare(con);
        ResultSet rs = dbQuery.executeQuery();
        if (rs.next()) {
            con.close();
            return (this.buildPlain(rs));
        } else {
            con.close();
            throw new SQLException("TrackFeedback with ID=" + id + " was not found");
        }

    }

    private int[] convertReasonTypesToInts(TrackFeedbackPlain feedback) {
        int[] list = new int[7];

        list[0] = convertToInt(feedback.isSongLiked(), feedback.isSongDisliked());
        list[1] = convertToInt(feedback.isArtistLiked(), feedback.isArtistDisliked());
        list[2] = convertToInt(feedback.isSpeedLiked(), feedback.isSpeedDisliked());
        list[3] = convertToInt(feedback.isGenreLiked(), feedback.isGenreDisliked());
        list[4] = convertToInt(feedback.isDynamicsLiked(), feedback.isDynamicsDisliked());
        list[5] = convertToInt(feedback.isPeriodLiked(), feedback.isPeriodDisliked());
        list[6] = convertToInt(feedback.isMoodLiked(), feedback.isMoodDisliked());

        return list;
    }



    private void toDB(TrackFeedbackPlain feedback) throws SQLException {

        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedback (userid, songid, radioid," +
                " feedbacksong, feedbackartist, feedbackspeed, feedbackgenre, feedbackdynamics, feedbackperiod, feedbackmood) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON Conflict (userid, songid, radioid) Do " +
                "UPDATE Set (feedbacksong, feedbackartist, feedbackspeed, feedbackgenre, " +
                "feedbackdynamics, feedbackperiod, feedbackmood, time) = " +
                "(?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);");
        
        addFeedback.setInt(1, feedback.getUserId());
        addFeedback.setInt(2, feedback.getTrackId());
        addFeedback.setInt(3, feedback.getRadioId());

        int[] values = convertReasonTypesToInts(feedback);
        int len = Math.min(values.length, 7);
        for (int i = 0; i < len; i++) { // feedback values for INSERT
            int index = i+4;
            addFeedback.setInt(index, values[i]);
        }
        for (int i = 0; i < len; i++) { // feedback values for UPDATE
            int index = i+4+7;
            addFeedback.setInt(index, values[i]);
        }

        addFeedback.executeUpdate();
        con.close();

    }
}
