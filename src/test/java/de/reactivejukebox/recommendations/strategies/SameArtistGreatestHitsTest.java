package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

public class SameArtistGreatestHitsTest {

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

        for (int i = 1; i < 8; i++) {
            Model.getInstance().getTracks().put(i, new Track(
                    i, "Track A" + i, artistA, albumA, "", "", 180, 4711 + i
            ));
        }

        Artist artistB = new Artist(2, "Artist B");
        Album albumB = new Album(2, "Album B", artistB);

        Model.getInstance().getArtists().put(artistB.getId(), artistB);
        Model.getInstance().getAlbums().put(albumB.getId(), albumB);

        for (int i = 8; i < 12; i++) {
            Model.getInstance().getTracks().put(i, new Track(
                    i, "Track B" + i, artistB, albumB, "", "", 190, 4211 - i
            ));
        }
    }

    @Test
    public void testGetRecommendations() throws Exception {
        List<Track> list = new ArrayList<>();
        Set<Track> history = Collections.emptySet(); // TODO test history awareness of SAGH
        list.add(Model.getInstance().getTracks().get(1));

        RecommendationStrategy algorithm = new SameArtistGreatestHits(history, list, Integer.MAX_VALUE);
        list = algorithm.getRecommendations();

        assertTrue(list.contains(Model.getInstance().getTracks().get(7)));
        assertFalse(list.contains(Model.getInstance().getTracks().get(8)));

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
