package de.reactivejukebox.model;

import java.sql.SQLException;

/**
 * Created by Sarah Graf on 14.07.2017.
 */
public class FeedbackTest {

    public static void main(String[] args) {
        TrackFeedbackPlain fedplain = new TrackFeedbackPlain();
        fedplain.setUserId(2);
        fedplain.setRadioId(1);
        fedplain.setTrackId(82);
        fedplain.setId(2);
        fedplain.setSongDisliked(true);
        fedplain.setArtistLiked(false);
        fedplain.setDynamicsLiked(true);
        Users users = new Users();
        /*try {
            users.initializeFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        Radios radios = new Radios(users);
        Tracks tracks = new Tracks();
        TrackFeedbacks testClass = new TrackFeedbacks(users, tracks, radios);
        try {
            testClass.put(fedplain);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TrackFeedback fb = testClass.get(fedplain.getId());

            // Tests:
            if (fedplain.isSongLiked() != fb.isSongLiked())
                System.err.println("Fehler bei 'isSongLiked'");
            else if (fedplain.isArtistLiked() != fb.isArtistDisliked())
                System.err.println("Fehler bei 'isArtistLiked'");
            else
                System.err.println("Tests passed!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
