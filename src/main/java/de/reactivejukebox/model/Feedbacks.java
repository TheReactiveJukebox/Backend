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

public class Feedbacks implements Iterable<Feedback> {
    protected Users users;
    protected Radios radios;
    protected PreparedStatementBuilder stmnt;
    protected Connection con;
    protected ConcurrentHashMap<Integer, Feedback> feedbackById;
    protected ConcurrentHashMap<Integer, ArrayList<Feedback>> feedbacksByUserId;
    protected ConcurrentHashMap<Integer, ArrayList<Feedback>> feedbacksByRadioId;

    public Feedbacks(Users users, Radios radios) {
        feedbackById = new ConcurrentHashMap<>();
        feedbacksByUserId = new ConcurrentHashMap<>();
        feedbacksByRadioId = new ConcurrentHashMap<>();
        this.users = users;
        this.radios = radios;
    }

    public Feedback put(FeedbackPlain feedback) throws SQLException {
        toDB(feedback);
        feedback = fromDB(feedback.getId());
        Feedback newFeedback = build(feedback);

        feedbackById.putIfAbsent(newFeedback.getId(), newFeedback);
        this.putIfAbsent(feedbacksByUserId, newFeedback.getUser().getId(), newFeedback);
        this.putIfAbsent(feedbacksByRadioId, newFeedback.getRadio().getId(), newFeedback);
        return newFeedback;
    }

    private void putIfAbsent(ConcurrentHashMap<Integer, ArrayList<Feedback>> hashMap, Integer id, Feedback feedback) {
        ArrayList<Feedback> tmpList;
        if (hashMap.containsKey(id)) {
            hashMap.get(id).add(feedback);
        } else {
            tmpList = new ArrayList<>();
            tmpList.add(feedback);
            hashMap.put(id, tmpList);
        }

    }

    public Feedback get(int id) throws SQLException {
        Feedback feedback;
        if (feedbackById.containsKey(id)) {
            feedback = feedbackById.get(id);
        } else {
            feedback = build(fromDB(id));
            feedbackById.putIfAbsent(feedback.getId(), feedback);
            this.putIfAbsent(feedbacksByUserId, feedback.getUser().getId(), feedback);
            this.putIfAbsent(feedbacksByRadioId, feedback.getRadio().getId(), feedback);
        }
        return feedback;
    }

    public ArrayList<Feedback> getByUserId(int id) throws SQLException {
        Feedback tmpFeedback;
        ArrayList<Feedback> feedbacks;
        ArrayList<FeedbackPlain> feedbacksPlain;
        if (feedbacksByUserId.containsKey(id)) {
            feedbacks = feedbacksByUserId.get(id);
        } else {
            feedbacksPlain = this.fromDbByUserId(id);
            feedbacks = new ArrayList<>();
            for (FeedbackPlain f : feedbacksPlain) {
                tmpFeedback = this.build(f);
                feedbacks.add(tmpFeedback);
                feedbackById.putIfAbsent(tmpFeedback.getId(), tmpFeedback);
                this.putIfAbsent(feedbacksByUserId, tmpFeedback.getUser().getId(), tmpFeedback);
                this.putIfAbsent(feedbacksByRadioId, tmpFeedback.getRadio().getId(), tmpFeedback);
            }
        }
        return feedbacks;
    }

    public Feedback get(FeedbackPlain feedback) throws SQLException {
        return get(feedback.getId());
    }


    @Override
    public Iterator<Feedback> iterator() {
        return feedbackById.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super Feedback> consumer) {
        feedbackById.values().forEach(consumer);
    }

    @Override
    public Spliterator<Feedback> spliterator() {
        return feedbackById.values().spliterator();
    }

    public Stream<Feedback> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    private Feedback build(FeedbackPlain feedback) throws SQLException {
        Feedback newFeedback = new Feedback();

        newFeedback.setRadio(radios.get(feedback.getRadioId()));
        newFeedback.setUser(users.get(feedback.getUserId()));

        newFeedback.setSongDisliked(feedback.isSongDisliked());
        newFeedback.setSongLiked(feedback.isSongLiked());

        newFeedback.setArtistDisliked(feedback.isArtistDisliked());
        newFeedback.setArtistLiked(feedback.isArtistLiked());

        newFeedback.setSpeedDisliked(feedback.isSpeedDisliked());
        newFeedback.setSpeedLiked(feedback.isSpeedLiked());

        newFeedback.setGenreDisliked(feedback.isGenreDisliked());
        newFeedback.setGenreLiked(feedback.isGenreLiked());

        newFeedback.setDynamicsDisliked(feedback.isDynamicsDisliked());
        newFeedback.setDynamicsLiked(feedback.isDynamicsLiked());

        newFeedback.setPeriodDisliked(feedback.isPeriodDisliked());
        newFeedback.setPeriodLiked(feedback.isPeriodLiked());

        newFeedback.setMoodDisliked(feedback.isMoodDisliked());
        newFeedback.setMoodLiked(feedback.isMoodLiked());

        return newFeedback;
    }

    private ArrayList<Feedback> build(ArrayList<FeedbackPlain> feedbackList) throws SQLException {
        ArrayList<Feedback> newList = new ArrayList<>();
        Iterator<FeedbackPlain> iterator = feedbackList.listIterator();
        while (iterator.hasNext()) {
            newList.add(build(iterator.next()));
        }
        return newList;
    }

    private ArrayList<FeedbackPlain> fromDbByUserId(int id) throws SQLException {
        ArrayList<FeedbackPlain> feedbacks = new ArrayList<>();
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

    private FeedbackPlain buildPlain(ResultSet rs) throws SQLException {
        FeedbackPlain feedback = new FeedbackPlain();
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

    private ArrayList<FeedbackPlain> fromDbByRadioId(int id) throws SQLException {
        ArrayList<FeedbackPlain> feedbacks = new ArrayList<>();
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


    private FeedbackPlain fromDB(int id) throws SQLException {
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
            throw new SQLException("Feedback with ID=" + id + " was not found");
        }

    }

    private void toDB(FeedbackPlain feedback) throws SQLException {
        //TODO: Write feedback to DB

        /* example
        con = DatabaseProvider.getInstance().getDatabase().getConnection();
        PreparedStatement addFeedback = con.prepareStatement("INSERT INTO radio (userid, israndom) VALUES (?, ?);");
        addUser.setInt(1, radio.getUserId());
        addUser.setBoolean(2, radio.isRandom());
        addUser.executeUpdate();
        con.close();
        */
    }
}
