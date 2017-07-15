package de.reactivejukebox.model;

/**
 * Created by Ben Wilkes on 13.07.2017.
 */

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
    protected PreparedStatementBuilder stmnt;
    protected Connection con;
    protected ConcurrentHashMap<Integer, TrackFeedback> feedbackById;
    protected ConcurrentHashMap<Integer, ArrayList<TrackFeedback>> feedbacksByUserId;
    protected ConcurrentHashMap<Integer, ArrayList<TrackFeedback>> feedbacksByRadioId;

    public TrackFeedbacks(Users users, Radios radios) {
        feedbackById = new ConcurrentHashMap<>();
        feedbacksByUserId = new ConcurrentHashMap<>();
        feedbacksByRadioId = new ConcurrentHashMap<>();
        this.users = users;
        this.radios = radios;
    }

    public TrackFeedback put(TrackFeedbackPlain feedback) throws SQLException {
        toDB(feedback);
        feedback = fromDB(feedback.getId());
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

        newTrackFeedback.setSongDisliked(feedback.isSongDisliked());
        newTrackFeedback.setSongLiked(feedback.isSongLiked());

        newTrackFeedback.setArtistDisliked(feedback.isArtistDisliked());
        newTrackFeedback.setArtistLiked(feedback.isArtistLiked());

        newTrackFeedback.setSpeedDisliked(feedback.isSpeedDisliked());
        newTrackFeedback.setSpeedLiked(feedback.isSpeedLiked());

        newTrackFeedback.setGenreDisliked(feedback.isGenreDisliked());
        newTrackFeedback.setGenreLiked(feedback.isGenreLiked());

        newTrackFeedback.setDynamicsDisliked(feedback.isDynamicsDisliked());
        newTrackFeedback.setDynamicsLiked(feedback.isDynamicsLiked());

        newTrackFeedback.setPeriodDisliked(feedback.isPeriodDisliked());
        newTrackFeedback.setPeriodLiked(feedback.isPeriodLiked());

        newTrackFeedback.setMoodDisliked(feedback.isMoodDisliked());
        newTrackFeedback.setMoodLiked(feedback.isMoodLiked());

        return newTrackFeedback;
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

    private TrackFeedbackPlain buildPlain(ResultSet rs) throws SQLException {
        TrackFeedbackPlain feedback = new TrackFeedbackPlain();
        feedback.setId(rs.getInt("id"));
        feedback.setUserId(rs.getInt("userid"));
        feedback.setRadioId(rs.getInt("radioid"));

        if (rs.getInt("feedbacksong") > 0) {
            feedback.setSongLiked(true);
        } else if (rs.getInt("feedbacksong") < 0) {
            feedback.setSongDisliked(true);
        }

        if (rs.getInt("feedbackartist") > 0) {
            feedback.setArtistLiked(true);
        } else if (rs.getInt("feedbackartist") < 0) {
            feedback.setArtistDisliked(true);
        }

        if (rs.getInt("feedbackspeed") > 0) {
            feedback.setSpeedLiked(true);
        } else if (rs.getInt("feedbackspeed") < 0) {
            feedback.setSpeedDisliked(true);
        }

        if (rs.getInt("feedbackgenre") > 0) {
            feedback.setGenreLiked(true);
        } else if (rs.getInt("feedbackgenre") < 0) {
            feedback.setGenreDisliked(true);
        }

        if (rs.getInt("feedbackdynamics") > 0) {
            feedback.setDynamicsLiked(true);
        } else if (rs.getInt("feedbackdynamics") < 0) {
            feedback.setDynamicsDisliked(true);
        }

        if (rs.getInt("feedbackperiod") > 0) {
            feedback.setPeriodLiked(true);
        } else if (rs.getInt("feedbackperiod") < 0) {
            feedback.setPeriodDisliked(true);
        }

        if (rs.getInt("feedbackmood") > 0) {
            feedback.setMoodLiked(true);
        } else if (rs.getInt("feedbackmood") < 0) {
            feedback.setMoodDisliked(true);
        }

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
        if (feedback.isSongLiked()) list[0] = 1;
        if (feedback.isSongDisliked()) list[0] = -1;
        if (!feedback.isSongLiked() && !feedback.isSongDisliked()) list[0] = 0;

        if (feedback.isArtistLiked()) list[1] = 1;
        if (feedback.isArtistDisliked()) list[1] = -1;
        if (!feedback.isArtistLiked() && !feedback.isArtistDisliked()) list[1] = 0;

        if (feedback.isSpeedLiked()) list[2] = 1;
        if (feedback.isSpeedDisliked()) list[2] = -1;
        if (!feedback.isSpeedLiked() && !feedback.isSpeedDisliked()) list[2] = 0;

        if (feedback.isGenreLiked()) list[3] = 1;
        if (feedback.isGenreDisliked()) list[3] = -1;
        if (!feedback.isGenreLiked() && !feedback.isGenreDisliked()) list[3] = 0;

        if (feedback.isDynamicsLiked()) list[4] = 1;
        if (feedback.isDynamicsDisliked()) list[4] = -1;
        if (!feedback.isDynamicsLiked() && !feedback.isDynamicsDisliked()) list[4] = 0;

        if (feedback.isPeriodLiked()) list[5] = 1;
        if (feedback.isPeriodDisliked()) list[5] = -1;
        if (!feedback.isPeriodLiked() && !feedback.isPeriodDisliked()) list[5] = 0;

        if (feedback.isMoodLiked()) list[6] = 1;
        if (feedback.isMoodDisliked()) list[6] = -1;
        if (!feedback.isMoodLiked() && !feedback.isMoodDisliked()) list[6] = 0;

        return list;
    }

    private void toDB(TrackFeedbackPlain feedback) throws SQLException {
        //TODO: If TrackFeedback to given song already exists, update instead of insert
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO feedback (userid, songid, radioid, " +
                "feedbacksong, feedbackartist, feedbackspeed, feedbackgenre, feedbackdynamics, feedbackperiod," +
                "feedbackmood) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        addFeedback.setInt(1, feedback.getUserId());
        addFeedback.setInt(2, feedback.getId());
        addFeedback.setInt(3, feedback.getRadioId());

        int[] values = convertReasonTypesToInts(feedback);
        int len = Math.min(values.length, 7);
        for (int i = 0; i < len; i++) {
            addFeedback.setInt(i + 4, values[i]);
        }

        addFeedback.executeUpdate();
        con.close();

    }
}
