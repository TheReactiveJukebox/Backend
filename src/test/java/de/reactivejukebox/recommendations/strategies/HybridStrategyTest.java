package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.RecommendationStrategyFactory;
import de.reactivejukebox.recommendations.Recommendations;
import org.mockito.Mockito;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static de.reactivejukebox.TestTools.setModelInstance;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class HybridStrategyTest {


    RecommendationStrategyFactory anyFactory;
    HybridStrategy anyHybrid;

    @BeforeClass
    public void setUp() {
        Artists artists = new Artists();
        Tracks tracks = new Tracks();
        Albums albums = new Albums();
        Users users = new Users();
        Radios radios = new Radios(users);
        HistoryEntries history = Mockito.mock(HistoryEntries.class);

        try {
            Mockito.when(history.getListByRadioId(Mockito.anyInt())).thenReturn(new ArrayList<>());
        } catch (Exception e) {
            // just to make this compile, no exceptions will be thrown
        }

        artists.put(1, new Artist(1, "Artist A"));
        albums.put(1, new Album(1, "Album A", artists.get(1)));
        tracks.put(1, new Track(1, "Track A1", artists.get(1), albums.get(1), "", "", 500, 17263, new Date(), 100, 0.5f, "",""));

        Model m = Mockito.mock(Model.class);
        Mockito.when(m.getArtists()).thenReturn(artists);
        Mockito.when(m.getTracks()).thenReturn(tracks);
        Mockito.when(m.getAlbums()).thenReturn(albums);
        Mockito.when(m.getUsers()).thenReturn(users);
        Mockito.when(m.getRadios()).thenReturn(radios);
        Mockito.when(m.getUsers()).thenReturn(users);
        Mockito.when(m.getHistoryEntries()).thenReturn(history);

        setModelInstance(m);

        Radio r = new Radio(1, new User(), null, null, null, null, null, null, null, null, null, StrategyType.HYBRID);
        anyFactory = new RecommendationStrategyFactory(r, Collections.emptyList());
        anyHybrid = new HybridStrategy(anyFactory, null, null, 100);
    }

    @Test
    public void testCreation() {
        // We test whether or not the creation process works smoothly.
        // Let's say some parts of the model aren't particularly well designed for testing.
        // If this fails, there is probably something wrong with the model.
        System.err.println(("The following NullPointerException is expected."));
        RecommendationStrategy algorithm = anyFactory.createStrategy(StrategyType.HYBRID, 0);
        assertTrue(algorithm instanceof HybridStrategy);
    }

    @Test
    public void testGatherAlgorithmResults() throws Exception {
        HashMap<Track, Float> results = new HashMap<>();

        anyHybrid.gatherAlgorithmResults(results, StrategyType.RANDOM);

        assertFalse(results.isEmpty());
    }

    @Test
    public void testCalculateLinearModifier() throws Exception {
        float a = 1.0f;
        float b = anyHybrid.calculateLinearModifier(HybridStrategy.FeedbackModifier.SKIP, 0);
        assertEquals(b, a);

        a = anyHybrid.calculateLinearModifier(HybridStrategy.FeedbackModifier.SKIP, 1);
        assertTrue(a < b);

        b = anyHybrid.calculateLinearModifier(HybridStrategy.FeedbackModifier.SKIP, Integer.MAX_VALUE);
        assertTrue(b < a);
        assertTrue(b > 0);
    }

    @Test
    public void testCalculateGaussianModifier() throws Exception{
        for (float x = -1f;x<1f;x=x+0.1f){
            assertTrue(anyHybrid.calculateGaussianModifier(x,0,2)<=1);
            if (x==0){
                assertTrue(anyHybrid.calculateGaussianModifier(x,0,2)==1);
            }
        }
    }

    @Test
    public void testApplyUserFeedback() throws Exception {
        UserProfile mockProfile = Mockito.mock(UserProfile.class);

        // let's say only the track was liked
        Mockito.when(mockProfile.getTrackFeedback(Mockito.anyInt())).thenReturn(1);

        // Nothing else was done to change the weight. We give the implementation the benefit of the doubt.
        Mockito.when(mockProfile.getAlbumFeedback(Mockito.anyInt())).thenReturn(0);
        Mockito.when(mockProfile.getMoodFeedback(Mockito.anyFloat(), Mockito.anyFloat())).thenReturn(0);
        Mockito.when(mockProfile.getSpeedFeedback(Mockito.anyInt())).thenReturn(0);
        Mockito.when(mockProfile.getArtistFeedback(Mockito.anyInt())).thenReturn(0);
        Mockito.when(mockProfile.getDeleteFeedback(Mockito.anyInt())).thenReturn(0);
        Mockito.when(mockProfile.getHistory(Mockito.anyInt())).thenReturn(0);
        Mockito.when(mockProfile.getMultiSkipFeedback(Mockito.anyInt())).thenReturn(0);
        Mockito.when(mockProfile.getSkipFeedback(Mockito.anyInt())).thenReturn(0);
        Mockito.when(mockProfile.getGenreFeedback(Mockito.anyString())).thenReturn(0);

        Track t = Model.getInstance().getTracks().get(1);
        HashMap<Track, Float> results = new HashMap<>();
        results.put(t, 1.0f);

        anyHybrid.applyUserFeedback(results, mockProfile);
        assertTrue(Math.abs(results.get(t) - HybridStrategy.FeedbackModifier.LIKE_TRACK.value) < 0.001);
    }

    @Test
    public void testCalculateHistoryModifier() throws Exception {
        assertTrue(anyHybrid.calculateHistoryModifier(1) <= 0.01);
        for (int i = 1; i <= 180;i++){
            assertTrue(anyHybrid.calculateHistoryModifier(i) <= anyHybrid.calculateHistoryModifier(i+1));
            assertTrue(anyHybrid.calculateHistoryModifier(i) <= 1);
        }
    }

    @Test
    public void testApplyHistory() throws Exception {
        UserProfile mockProfile = Mockito.mock(UserProfile.class);

        //fake History Track with Track Id 1 ist the first played song Track with id 100 is the last played Song
        for(int i = 1; i< 100; i++){
            Mockito.when(mockProfile.getHistory(i)).thenReturn(100-i);
        }

        //Tracks 4 already played 1 new
        Track t0 = new Track();
        t0.setId(150);  //not yet played song
        Track t1 = new Track();
        t1.setId(25);   //about 75 songs played after
        Track t2 = new Track();
        t2.setId(30);   //about 70 songs played after
        Track t3 = new Track();
        t3.setId(98);   //second to last song played
        Track t4 = new Track();
        t4.setId(99);   //last song played


        HashMap<Track, Float> results = new HashMap<>();
        results.put(t1,2f);
        results.put(t2,5f);
        results.put(t3,1f);
        results.put(t4,1f);
        results.put(t0,1f);
        anyHybrid.applyHistory(results, mockProfile);
        assertTrue(results.get(t0) > results.get(t2));
        assertTrue(results.get(t2) > results.get(t1));
        assertTrue(results.get(t2) > results.get(t3));
        assertTrue(results.get(t3) > results.get(t4));

    }

    @Test(enabled = false) // TODO implement this test
    public void testGetRecommendations() throws Exception {
        User u = new User();
        Model.getInstance().getRadios();
        Radio r = new Radio(1, u, new String[0], 1800, 2080, 0f, 0f, 0f, 0f, 300f, new ArrayList<>(), StrategyType.HYBRID);

        RecommendationStrategyFactory factory = new RecommendationStrategyFactory(r, Collections.emptyList());
        RecommendationStrategy algorithm = factory.createStrategy(StrategyType.HYBRID, 100);

        Recommendations recs = algorithm.getRecommendations();
        List<Track> recommendedTracks = recs.getTracks();

        assertFalse(recommendedTracks.isEmpty());
    }

}