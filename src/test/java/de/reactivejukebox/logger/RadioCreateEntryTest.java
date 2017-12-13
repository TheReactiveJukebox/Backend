package de.reactivejukebox.logger;

import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.strategies.StrategyType;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class RadioCreateEntryTest extends EntryTest {
    Radio getRadioObj() {
        Radio radio = new Radio();
        radio.setId(4);
        radio.setUser(getUserObj());
        radio.setAlgorithm(StrategyType.RANDOM);
        return radio;
    }

    Track getTrackObj() {
        Artist artist = new Artist(73, "ATB");
        Album album = new Album(74, "Contact CD2", artist);
        Track t = new Track();
        t.setId(72);
        t.setTitle("Love the Silence");
        t.setArtist(artist);
        t.setAlbum(album);
        return t;
    }

    @Test
    public void testRadioField() {
        Entry e = new RadioCreateEntry(getUserObj(), getRadioObj());
        String[] s = e.getEntry();
        // Assert
        assertEquals("4", s[EntryCol.RADIO.ordinal()]);
    }

    @Test
    public void testAlgorithmField() {
        Entry e = new RadioCreateEntry(getUserObj(), getRadioObj());
        String[] s = e.getEntry();
        // Assert
        assertEquals(StrategyType.RANDOM.toString(), s[EntryCol.ALGORITHM.ordinal()]);
    }

    @Test
    public void testGenresField() {
        Radio radio = getRadioObj();
        String[] genres = {"Metal", "Black Metal"};
        radio.setGenres(genres);
        Entry e = new RadioCreateEntry(getUserObj(), radio);
        String[] s = e.getEntry();
        // Assert
        assertEquals("Metal,Black Metal", s[EntryCol.GENRE.ordinal()]);
    }

    @Test
    public void testYearsField() {
        Radio radio = getRadioObj();
        radio.setStartYear(1980);
        radio.setEndYear(1990);
        Entry e = new RadioCreateEntry(getUserObj(), radio);
        String[] s = e.getEntry();
        // Assert
        assertEquals("1980", s[EntryCol.YEAR_START.ordinal()]);
        assertEquals("1990", s[EntryCol.YEAR_END.ordinal()]);
    }

    @Test
    public void testDynamicField() {
        Radio radio = getRadioObj();
        radio.setDynamic((float) 0.25);
        Entry e = new RadioCreateEntry(getUserObj(), radio);
        String[] s = e.getEntry();
        // Assert
        assertEquals("0.25", s[EntryCol.DYNAMIC.ordinal()]);
    }

    @Test
    public void testArousalValenceField() {
        Radio radio = getRadioObj();
        radio.setArousal((float) 0.79);
        radio.setValence((float) -0.11);
        Entry e = new RadioCreateEntry(getUserObj(), radio);
        String[] s = e.getEntry();
        // Assert
        assertEquals("0.79", s[EntryCol.AROUSAL.ordinal()]);
        assertEquals("-0.11", s[EntryCol.VALENCE.ordinal()]);
    }

    @Test
    public void testSpeedField() {
        Radio radio = getRadioObj();
        radio.setMinSpeed((float) 200);
        radio.setMaxSpeed((float) 279);
        Entry e = new RadioCreateEntry(getUserObj(), radio);
        String[] s = e.getEntry();
        // Assert
        assertEquals("200.0", s[EntryCol.SPEED_MIN.ordinal()]);
        assertEquals("279.0", s[EntryCol.SPEED_MAX.ordinal()]);
    }

    @Test
    public void testSongField() {
        Radio radio = getRadioObj();
        List<Track> songs = new LinkedList<Track>();
        songs.add(getTrackObj());
        {
            Track t = getTrackObj();
            t.setId(55);
            t.setTitle("Foo");
            songs.add(t);
        }
        radio.setStartTracks(songs);
        Entry e = new RadioCreateEntry(getUserObj(), radio);
        String[] s = e.getEntry();
        // Assert
        assertEquals("72,55", s[EntryCol.SONG.ordinal()]);
    }

    @Test
    public void testJsonField() {
        Entry e = new RadioCreateEntry(getUserObj(), getRadioObj());
        String[] s = e.getEntry();
        assertNotNull(s[EntryCol.JSON.ordinal()]);
    }
}
