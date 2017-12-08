package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import org.mockito.Mockito;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static de.reactivejukebox.TestTools.setModelInstance;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class RandomTracksTest {

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

        for (int i = 1; i < TRACKSARTIST_A + 1; i++) {
            Model.getInstance().getTracks().put(i, new Track(
                    i, "Track A" + i, artistA, albumA, "", "", 180, 4711 + i, new Date(), 120, 0.9f
            ));
        }

        Artist artistB = new Artist(2, "Artist B");
        Album albumB = new Album(2, "Album B", artistB);

        Model.getInstance().getArtists().put(artistB.getId(), artistB);
        Model.getInstance().getAlbums().put(albumB.getId(), albumB);

        int _startindex = Model.getInstance().getTracks().size();
        for (int i = _startindex + 1; i < TRACKSARTIST_B + _startindex + 1; i++) {
            Model.getInstance().getTracks().put(i, new Track(
                    i, "Track B" + i, artistB, albumB, "", "", 191, 4211 - i, new Date(), 120, 0.9f
            ));
        }


        Radio radio = new Radio();

        ArrayList<HistoryEntry> history = new ArrayList<>();
        history.add(new HistoryEntry(1, Model.getInstance().getTracks().get(1), radio, new User(), new Timestamp(1))); //Add track to history
        history.add(new HistoryEntry(2, Model.getInstance().getTracks().get(2), radio, new User(), new Timestamp(2))); //Add track to history
        history.add(new HistoryEntry(3, Model.getInstance().getTracks().get(3), radio, new User(), new Timestamp(3))); //Add track to history


        Mockito.when(h.getListByRadioId(1)).thenReturn(new ArrayList<>());
        Mockito.when(h.getListByRadioId(2)).thenReturn(history);


    }

    @Test
    public void testListSize() throws Exception {
        List<Track> list;

        Radio radio = new Radio();
        radio.setId(1);

        //small number of requested tracks
        RecommendationStrategy algorithm = new RandomTracks(radio, new HashSet<>(), 5);
        list = algorithm.getRecommendations().getTracks(); //run algorithm
        assertTrue(list.size() == 5); //test list size

        // request no tracks
        algorithm = new RandomTracks(radio, new HashSet<>(), 0);
        list = algorithm.getRecommendations().getTracks(); //run algorithm
        assertTrue(list.size() == 0); //test list size

        // negative number of requested tracks
        algorithm = new RandomTracks(radio, new HashSet<>(), -42);
        list = algorithm.getRecommendations().getTracks(); //run algorithm
        assertTrue(list.size() == 0); //test list size

        //request all tracks
        algorithm = new RandomTracks(radio, new HashSet<>(), TRACKSARTIST_A + TRACKSARTIST_B);
        list = algorithm.getRecommendations().getTracks(); //run algorithm
        assertTrue(list.size() == (TRACKSARTIST_A + TRACKSARTIST_B)); //test list size

        //request more tracks than available (tracks could be added twice)
        algorithm = new RandomTracks(radio, new HashSet<>(), (int) ((TRACKSARTIST_A + TRACKSARTIST_B) * 1.5));
        list = algorithm.getRecommendations().getTracks(); //run algorithm
        assertTrue(list.size() == (int) ((TRACKSARTIST_A + TRACKSARTIST_B) * 1.5)); //test list size

        //request way more tracks than available (tracks could be added twice)
        algorithm = new RandomTracks(radio, new HashSet<>(), (int) ((TRACKSARTIST_A + TRACKSARTIST_B) * 3.2));
        list = algorithm.getRecommendations().getTracks(); //run algorithm
        assertTrue(list.size() == (int) ((TRACKSARTIST_A + TRACKSARTIST_B) * 3.2)); //test list size

    }

    @Test
    public void testRecommendationsWithHistory() throws Exception {
        /*
        Test if tracks contained in the history are not recommended by the algorithm
        (do not request more than available)
         */

        Radio radio = new Radio();
        radio.setId(2);

        List<Track> list;

        RecommendationStrategy algorithm = new RandomTracks(radio, new HashSet<>(), (TRACKSARTIST_A + TRACKSARTIST_B - 3));
        list = algorithm.getRecommendations().getTracks(); //run algorithm

        //Test tracks not from history
        assertFalse(list.contains(Model.getInstance().getTracks().get(1)));
        assertFalse(list.contains(Model.getInstance().getTracks().get(2)));
        assertFalse(list.contains(Model.getInstance().getTracks().get(3)));

        for (int i = 4; i < TRACKSARTIST_A + TRACKSARTIST_B + 1; i++) { //remaining songs in list
            //every other track should be in list
            assertTrue(list.contains(Model.getInstance().getTracks().get(i)));
        }
    }

    /**
     * Test if history tracks are recommended if more tracks are requested than available
     */
    @Test
    public void testRecommendationsWithHistory_RequestMoreSongsThanAvailable() {
        Radio radio = new Radio();
        radio.setId(2);

        Set<Track> allTracks = Model.getInstance().getTracks().stream().collect(Collectors.toSet());

        // Act
        RecommendationStrategy algorithm = new RandomTracks(radio, new HashSet<>(), allTracks.size());
        List<Track> result = algorithm.getRecommendations().getTracks(); //run algorithm

        // Assert
        assert result.containsAll(allTracks);
    }

    private static Track someTrack(int id) {
        Track track = new Track();
        track.setId(id);
        track.setTitle("Track " + id);
        return track;
    }

    @Test
    public void testRecommendationsWithHistory_RequestMoreSongsThanInDatabase() throws Exception {
        /*
        Test if history tracks and already recommended tracks are in list (extreme case)
         */
        List<Track> list;
        Radio radio = new Radio();
        radio.setId(2);

        RecommendationStrategy algorithm = new RandomTracks(radio, new HashSet<>(), (TRACKSARTIST_A + TRACKSARTIST_B + 5));
        list = algorithm.getRecommendations().getTracks(); //run algorithm

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

}