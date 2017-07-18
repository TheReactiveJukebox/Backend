package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class RandomTracksTest {

    public static int TRACKSARTIST_A = 8;
    public static int TRACKSARTIST_B = 8;

    @BeforeClass
    private void setUp() {
        Artists artists = new Artists();
        Tracks tracks = new Tracks();
        Albums albums = new Albums();

        Model m = Mockito.mock(Model.class);
        Mockito.when(m.getAlbums()).thenReturn(albums);
        Mockito.when(m.getArtists()).thenReturn(artists);
        Mockito.when(m.getTracks()).thenReturn(tracks);

        setModelInstance(m);

        Artist artistA = new Artist(1, "Artist A");
        Album albumA = new Album(1, "Album A", artistA);

        m.getArtists().put(artistA.getId(), artistA);
        m.getAlbums().put(albumA.getId(), albumA);

        for (int i = 1; i < TRACKSARTIST_A + 1; i++) {
            Model.getInstance().getTracks().put(i, new Track(
                    i, "Track A" + i, artistA, albumA, "", "", 180, 4711 + i
            ));
        }

        Artist artistB = new Artist(2, "Artist B");
        Album albumB = new Album(2, "Album B", artistB);

        Model.getInstance().getArtists().put(artistB.getId(), artistB);
        Model.getInstance().getAlbums().put(albumB.getId(), albumB);

        int _startindex = Model.getInstance().getTracks().size();
        for (int i = _startindex; i < TRACKSARTIST_B + _startindex + 1; i++) {
            Model.getInstance().getTracks().put(i, new Track(
                    i, "Track B" + i, artistB, albumB, "", "", 191, 4211 - i
            ));
        }

    }

    @Test
    public void testListSize() throws Exception {
        List<Track> list;
        Set<Track> history = new HashSet<>();

        //small number of requested tracks
        RecommendationStrategy algorithm = new RandomTracks(history, 5);
        list = algorithm.getRecommendations(); //run algorithm
        assertTrue(list.size() == 5); //test list size

        // request no tracks
        algorithm = new RandomTracks(history, 0);
        list = algorithm.getRecommendations(); //run algorithm
        assertTrue(list.size() == 0); //test list size

        // negative number of requested tracks
        algorithm = new RandomTracks(history, -42);
        list = algorithm.getRecommendations(); //run algorithm
        assertTrue(list.size() == 0); //test list size

        //request all tracks
        algorithm = new RandomTracks(history, TRACKSARTIST_A + TRACKSARTIST_B);
        list = algorithm.getRecommendations(); //run algorithm
        assertTrue(list.size() == (TRACKSARTIST_A + TRACKSARTIST_B)); //test list size

        //request more tracks than available (tracks could be added twice)
        algorithm = new RandomTracks(history, (int) ((TRACKSARTIST_A + TRACKSARTIST_B) * 1.5));
        list = algorithm.getRecommendations(); //run algorithm
        assertTrue(list.size() == (int) ((TRACKSARTIST_A + TRACKSARTIST_B) * 1.5)); //test list size

        //request way more tracks than available (tracks could be added twice)
        algorithm = new RandomTracks(history, (int) ((TRACKSARTIST_A + TRACKSARTIST_B) * 3.2));
        list = algorithm.getRecommendations(); //run algorithm
        assertTrue(list.size() == (int) ((TRACKSARTIST_A + TRACKSARTIST_B) * 3.2)); //test list size

    }

    @Test
    public void testRecommendationsWithHistory() throws Exception {
        /*
        Test if tracks contained in the history are not recommended by the algorithm
        (do not request more than available)
         */

        List<Track> list;
        Set<Track> history = new HashSet<>();
        history.add(Model.getInstance().getTracks().get(1)); //Add track to history
        history.add(Model.getInstance().getTracks().get(2)); //Add track to history
        history.add(Model.getInstance().getTracks().get(3)); //Add track to history

        RecommendationStrategy algorithm = new RandomTracks(history, (TRACKSARTIST_A + TRACKSARTIST_B - 3));
        list = algorithm.getRecommendations(); //run algorithm

        //Test tracks not from history
        assertFalse(list.contains(Model.getInstance().getTracks().get(1)));
        assertFalse(list.contains(Model.getInstance().getTracks().get(2)));
        assertFalse(list.contains(Model.getInstance().getTracks().get(3)));

        for (int i = 4; i < TRACKSARTIST_A + TRACKSARTIST_B + 1; i++) { //remaining songs in list
            //every other track should be in list
            assertTrue(list.contains(Model.getInstance().getTracks().get(i)));
        }
    }

    @Test
    public void testRecommendationsWithHistory_RequestMoreSongsThanAvailable() throws Exception {
        /*
        Test if history tracks are recommended if more tracks are requested than available
         */
        List<Track> list;
        Set<Track> history = new HashSet<>();
        history.add(Model.getInstance().getTracks().get(1)); //Add track to history
        history.add(Model.getInstance().getTracks().get(2)); //Add track to history
        history.add(Model.getInstance().getTracks().get(3)); //Add track to history

        RecommendationStrategy algorithm = new RandomTracks(history, (TRACKSARTIST_A + TRACKSARTIST_B));
        list = algorithm.getRecommendations(); //run algorithm

        for (int i = 1; i < TRACKSARTIST_A + TRACKSARTIST_B + 1; i++) { //all songs
            //all songs should be in list, even tracks from the history
            assertTrue(list.contains(Model.getInstance().getTracks().get(i)));
        }
    }

    @Test
    public void testRecommendationsWithHistory_RequestMoreSongsThanInDatabase() throws Exception {
        /*
        Test if history tracks and already recommended tracks are in list (extreme case)
         */
        List<Track> list;
        Set<Track> history = new HashSet<>();
        history.add(Model.getInstance().getTracks().get(1)); //Add track to history
        history.add(Model.getInstance().getTracks().get(2)); //Add track to history
        history.add(Model.getInstance().getTracks().get(3)); //Add track to history

        RecommendationStrategy algorithm = new RandomTracks(history, (TRACKSARTIST_A + TRACKSARTIST_B + 5));
        list = algorithm.getRecommendations(); //run algorithm

        int trackcount = 0;
        for (int i = 1; i < TRACKSARTIST_A + TRACKSARTIST_B + 1; i++) { //all songs
            //all songs should be in list, even tracks from the history. Some tracks can be contained multiple times
            assertTrue(list.contains(Model.getInstance().getTracks().get(i))); //contains track at all
            while (list.contains(Model.getInstance().getTracks().get(i))) {
                list.remove(Model.getInstance().getTracks().get(i)); //remove track
                trackcount++;
            }
        }
        assertTrue(trackcount == (TRACKSARTIST_A + TRACKSARTIST_B + 5));
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