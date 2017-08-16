package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

public class SameArtistGreatestHitsTest {
    public static int TRACKSARTIST_A = 8;
    public static int TRACKSARTIST_B = 8;

    @BeforeClass
    private void setUp() throws SQLException {
        Artists artists = new Artists();
        Tracks tracks = new Tracks();
        Albums albums = new Albums();

        HistoryEntries h = mock(HistoryEntries.class);

        Model m = mock(Model.class);
        Mockito.when(m.getAlbums()).thenReturn(albums);
        Mockito.when(m.getArtists()).thenReturn(artists);
        Mockito.when(m.getTracks()).thenReturn(tracks);
        Mockito.when(m.getHistoryEntries()).thenReturn(h);

        setModelInstance(m);

        Artist artistA = new Artist(1, "Artist A");
        Album albumA = new Album(1, "Album A", artistA);

        m.getArtists().put(artistA.getId(), artistA);
        m.getAlbums().put(albumA.getId(), albumA);

        for (int i = 0; i < TRACKSARTIST_A ; i++) {
            Model.getInstance().getTracks().put(i, new Track(
                    i, "Track A" + i, artistA, albumA, "", "", 180, 4711 + i, new Date()
            ));
        }

        Artist artistB = new Artist(2, "Artist B");
        Album albumB = new Album(2, "Album B", artistB);

        Model.getInstance().getArtists().put(artistB.getId(), artistB);
        Model.getInstance().getAlbums().put(albumB.getId(), albumB);

        int _startindex = Model.getInstance().getTracks().size();
        for (int i = _startindex ; i < TRACKSARTIST_B + _startindex; i++) {
            Model.getInstance().getTracks().put(i, new Track(
                    i, "Track B" + i, artistB, albumB, "", "", 191, 4211 - i, new Date()
            ));
        }


        Radio radio = new Radio();

        ArrayList<HistoryEntry> history = new ArrayList<>();
        history.add(new HistoryEntry(1,Model.getInstance().getTracks().get(1),radio,new User(),new Timestamp(1))); //Add track to history
        history.add(new HistoryEntry(2,Model.getInstance().getTracks().get(2),radio,new User(),new Timestamp(2))); //Add track to history
        history.add(new HistoryEntry(3,Model.getInstance().getTracks().get(9),radio,new User(),new Timestamp(3))); //Add track to history



        Mockito.when(h.getListByRadioId(1)).thenReturn(new ArrayList<>());
        Mockito.when(h.getListByRadioId(2)).thenReturn(history);


    }

    @Test
    public void testGetRecommendations() throws Exception {
        List<Track> list = new ArrayList<>();

        Radio radio = new Radio();
        radio.setId(2);
        radio.getStartTracks().add(Model.getInstance().getTracks().get(1));

        RecommendationStrategy algorithm = new SameArtistGreatestHits(radio, new HashSet<>(), 5);
        list = algorithm.getRecommendations();

        assertTrue(list.contains(Model.getInstance().getTracks().get(7)));
        assertFalse(list.contains(Model.getInstance().getTracks().get(8)));
        assertFalse(list.contains(Model.getInstance().getTracks().get(2)));
    }

    @Test
    public void testGetRecommendationsMoreTracks() throws Exception {
        List<Track> list = new ArrayList<>();

        Radio radio = new Radio();
        radio.setId(2);
        radio.getStartTracks().add(Model.getInstance().getTracks().get(1));

        RecommendationStrategy algorithm = new SameArtistGreatestHits(radio, new HashSet<>(), Integer.MAX_VALUE);
        list = algorithm.getRecommendations();

        assertTrue(list.contains(Model.getInstance().getTracks().get(7)));
        assertFalse(list.contains(Model.getInstance().getTracks().get(8)));
        assertTrue(list.contains(Model.getInstance().getTracks().get(3)));
    }

    @AfterClass
    public void tearDown() {
        setModelInstance(null);
    }

    private void setModelInstance(Model m) {
        try {
            Field instance = Model.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, m);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Could not set instance field of model class using reflection!");
            Assert.fail();
        }
    }
}
