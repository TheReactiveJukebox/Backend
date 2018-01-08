package de.reactivejukebox.model;

import de.reactivejukebox.recommendations.strategies.StrategyType;
import org.mockito.Mockito;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static de.reactivejukebox.TestTools.setModelInstance;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class RadioTest {

    @BeforeClass
    private void setUp() throws SQLException {
        Genres genres = new Genres();
        genres.put("genre1", "meta1");
        genres.put("genre2", "meta1");
        genres.put("genre3", "meta2");
        genres.put("genre4", "meta2");

        Model m = mock(Model.class);
        Mockito.when(m.getGenres()).thenReturn(genres);

        setModelInstance(m);

    }

    @Test
    private void GenreTest() {
        String[] radioGenres = new String[1];
        radioGenres[0] = "meta1";
        Radio r = new Radio(1, new User(), radioGenres, null, null, 0f, null, null, null, null, new LinkedList<>(), StrategyType.RANDOM);

        Track t1 = new Track(1, "Title1", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f,"","");
        Track t2 = new Track(2, "Title2", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f,"","");
        Track t3 = new Track(3, "Title3", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f,"","");
        Track t4 = new Track(4, "Title4", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f,"","");
        t1.getGenres().add("genre1");
        t2.getGenres().add("genre2");
        t3.getGenres().add("genre3");
        t4.getGenres().add("genre4");

        Set<Track> tracks = new HashSet<>();
        tracks.add(t1);
        tracks.add(t2);
        tracks.add(t3);
        tracks.add(t4);

        Set<Track> result = r.filter(tracks.stream()).collect(Collectors.toSet());
        assertFalse(result.contains(t3));
        assertFalse(result.contains(t4));
        assertTrue(result.contains(t1));
        assertTrue(result.contains(t2));
    }

    @Test
    private void PublishedTest() {
        String[] radioGenres = new String[0];
        //radioGenres[0] = "meta1";
        Radio r = new Radio(1, new User(), radioGenres, 1990, 2000, 0f, null, null, null, null, new LinkedList<Track>(), StrategyType.RANDOM);

        Date d1 = new GregorianCalendar(1990, 1, 1).getTime();
        Date d2 = new GregorianCalendar(2000, 1, 1).getTime();
        Date d3 = new GregorianCalendar(1989, 1, 1).getTime();
        Date d4 = new GregorianCalendar(2001, 1, 1).getTime();

        Track t1 = new Track(1, "Title1", new Artist(), new Album(), "blacover", "blahash", 50, 0, d1, 120, 0.9f,"","");
        Track t2 = new Track(2, "Title2", new Artist(), new Album(), "blacover", "blahash", 50, 0, d2, 120, 0.9f,"","");
        Track t3 = new Track(3, "Title3", new Artist(), new Album(), "blacover", "blahash", 50, 0, d3, 120, 0.9f,"","");
        Track t4 = new Track(4, "Title4", new Artist(), new Album(), "blacover", "blahash", 50, 0, d4, 120, 0.9f,"","");
        Track t5 = new Track(5, "Title5", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f,"","");

        Set<Track> tracks = new HashSet<>();
        tracks.add(t1);
        tracks.add(t2);
        tracks.add(t3);
        tracks.add(t4);
        tracks.add(t5);

        Set<Track> result = r.filter(tracks.stream()).collect(Collectors.toSet());
        assertFalse(result.contains(t3));
        assertFalse(result.contains(t4));
        assertTrue(result.contains(t1));
        assertTrue(result.contains(t2));
        assertFalse(result.contains(t5));
    }


    @AfterClass
    public void tearDown() {
        setModelInstance(null);
    }
}
