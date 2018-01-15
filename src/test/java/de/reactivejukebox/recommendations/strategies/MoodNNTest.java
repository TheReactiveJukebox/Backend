package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import org.mockito.Mockito;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static de.reactivejukebox.TestTools.setModelInstance;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertTrue;

public class MoodNNTest {

    private final float[] AROUSALS = {0.4f, 0.4f, -0.4f, -0.4f};
    private final float[] VALENCES = {-0.4f, 0.4f, 0.4f, -0.4f};

    @BeforeClass
    private void setUpModel() throws SQLException {
        Tracks tracks = new Tracks();

        Model m = mock(Model.class);
        HistoryEntries h = mock(HistoryEntries.class);
        Mockito.when(m.getTracks()).thenReturn(tracks);
        Mockito.when(m.getHistoryEntries()).thenReturn(h);
        Mockito.when(h.getListByRadioId(1)).thenReturn(new ArrayList<>());

        setModelInstance(m);

        Random rng = new Random(5);
        Album stub = new Album();

        Artist checker;
        float a, v;
        for (int i = 0; i < 4; i++) {
            checker = new Artist(i + 1, "Artist");
            for (int j = 0; j < 100; j++) {
                a = AROUSALS[i] - 0.05f + (rng.nextFloat() * 0.1f);
                v = VALENCES[i] - 0.05f + (rng.nextFloat() * 0.1f);
                Model.getInstance().getTracks().put(i * 100 + j, new Track(i * 100 + j, "", checker, stub, "", "",
                        0, 0, null, 0f, 0f, "","", v, a));
            }
        }

        //Model.getInstance().getTracks().put(500, new Track());

        ArrayList<HistoryEntry> history = new ArrayList<>();
        for (int i = 0; i < 80; i++)
            history.add(new HistoryEntry(i, Model.getInstance().getTracks().get(i), new Radio(), new User(), new Timestamp(1))); //Add track to history

        Mockito.when(h.getListByRadioId(2)).thenReturn(history);
    }

    @Test
    public void TestNNModel() throws Exception {

        Radio radio = new Radio();
        radio.setId(1);
        radio.getStartTracks().add(new Track(888, "", null, null, "", "",
                0, 0, null, 0f, 0f, "","", -0.4f, 0.4f));

        RecommendationStrategy strat = new MoodNN(radio, new ArrayList<Track>(), 20);
        List<Track> result = strat.getRecommendations().getTracks();
        List<Float> scores = strat.getRecommendations().getScores();

        assertTrue(scores.size() == result.size());
        assertTrue(result.size() == 20);
        float dist = -1;
        for (Track e : result) {
            assertTrue(e.getArtist().getId() == 1);
            float current = Math.abs(e.getArousal() - 0.4f) + Math.abs(e.getValence() + 0.4f);
            assertTrue(current >= dist);
            dist = current;
        }

        float prevScore = 1;
        for (Float score : scores) {
            assertTrue(score <= 1 && score >= 0);
            assertTrue(score <= prevScore);
            prevScore = score;
        }

        radio.getStartTracks().clear();
        radio.getStartTracks().add(new Track(888, "", null, null, "", "",
                0, 0, null, 0f, 0f, "","", 0.4f, 0.4f));

        result = strat.getRecommendations().getTracks();
        scores = strat.getRecommendations().getScores();

        assertTrue(scores.size() == result.size());
        assertTrue(result.size() == 20);
        for (Track e : result) {
            assertTrue(e.getArtist().getId() == 2);
        }

        prevScore = 1;
        for (Float score : scores) {
            assertTrue(score <= 1 && score >= 0);
            assertTrue(score <= prevScore);
            prevScore = score;
        }

        radio.getStartTracks().clear();
        radio.getStartTracks().add(new Track(888, "", null, null, "", "",
                0, 0, null, 0f, 0f, "","", 0.4f, -0.4f));

        result = strat.getRecommendations().getTracks();
        scores = strat.getRecommendations().getScores();

        assertTrue(scores.size() == result.size());
        assertTrue(result.size() == 20);
        for (Track e : result) {
            assertTrue(e.getArtist().getId() == 3);
        }

        prevScore = 1;
        for (Float score : scores) {
            assertTrue(score <= 1 && score >= 0);
            assertTrue(score <= prevScore);
            prevScore = score;
        }

        radio.getStartTracks().clear();
        radio.getStartTracks().add(new Track(888, "", null, null, "", "",
                0, 0, null, 0f, 0f, "","", -0.4f, -0.4f));

        result = strat.getRecommendations().getTracks();
        scores = strat.getRecommendations().getScores();

        assertTrue(scores.size() == result.size());
        assertTrue(result.size() == 20);
        for (Track e : result) {
            assertTrue(e.getArtist().getId() == 4);
        }

        prevScore = 1;
        for (Float score : scores) {
            assertTrue(score <= 1 && score >= 0);
            assertTrue(score <= prevScore);
            prevScore = score;
        }


        radio.getStartTracks().add(new Track(888, "", null, null, "", "",
                0, 0, null, 0f, 0f, "","", -0.4f, 0.4f));
        assertTrue(result.size() == 20);
        result = strat.getRecommendations().getTracks();
        scores = strat.getRecommendations().getScores();

        assertTrue(scores.size() == result.size());
        for (Track e : result) {
            assertTrue(e.getArtist().getId() == 1 || e.getArtist().getId() == 4);
        }

        prevScore = 1;
        for (Float score : scores) {
            assertTrue(score <= 1 && score >= 0);
            assertTrue(score <= prevScore);
            prevScore = score;
        }

    }

    @Test
    public void TestNNHistory() throws Exception {
        Radio radio = new Radio();
        radio.setId(2);
        radio.getStartTracks().add(new Track(888, "", null, null, "", "",
                0, 0, null, 0f, 0f, "","",-0.4f, 0.4f));

        RecommendationStrategy strat = new MoodNN(radio, new ArrayList<Track>(), 20);
        List<Track> result = strat.getRecommendations().getTracks();
        for (Track e : result) {
            assertTrue(e.getId() >= 80 && e.getId() < 101);
        }
    }

    @Test
    public void TestConstructor() throws Exception{
        Radio radio = new Radio();
        radio.setArousal(0.01f);
        radio.setValence(-0.01f);

        RecommendationStrategy strat = new MoodNN(radio, new ArrayList<>(),20);
        Field selectedField = MoodNN.class.getDeclaredField("selectedTracks");
        selectedField.setAccessible(true);
        Collection<Track> selected = (Collection<Track>) selectedField.get(strat);
        assertTrue(selected.size() == 1);
        for (Track t:selected){
            assertTrue(t.getArtist().getId()==1);
        }

        radio.setValence(0.01f);
        strat = new MoodNN(radio, new ArrayList<>(),20);
        selected = (Collection<Track>) selectedField.get(strat);
        assertTrue(selected.size() == 1);
        for (Track t:selected){
            assertTrue(t.getArtist().getId()==2);
        }

        radio.setArousal(-0.01f);
        strat = new MoodNN(radio, new ArrayList<>(),20);
        selected = (Collection<Track>) selectedField.get(strat);
        assertTrue(selected.size() == 1);
        for (Track t:selected){
            assertTrue(t.getArtist().getId()==3);
        }

        radio.setValence(-0.01f);
        strat = new MoodNN(radio, new ArrayList<>(),20);
        selected = (Collection<Track>) selectedField.get(strat);
        assertTrue(selected.size() == 1);
        for (Track t:selected){
            assertTrue(t.getArtist().getId()==4);
        }
    }
}
