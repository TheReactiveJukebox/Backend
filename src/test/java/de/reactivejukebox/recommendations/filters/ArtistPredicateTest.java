package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.Album;
import de.reactivejukebox.model.Artist;
import de.reactivejukebox.model.Track;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ArtistPredicateTest {

    @Test
    private void TestArtistPredicateTRUE() {
        Artist a1 = new Artist(1, "test1");
        Artist a2 = new Artist(2, "test2");
        Track t1 = new Track(1, "Title1", a1, new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);
        Track t2 = new Track(2, "Title2", a2, new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);
        Track t3 = new Track(3, "Title3", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);

        Predicate<Track> artistPred = new ArtistPredicate(a1);
        assertTrue(artistPred.test(t1));

    }

    @Test
    private void TestArtistPredicateFALSE() {
        Artist a1 = new Artist(1, "test1");
        Artist a2 = new Artist(2, "test2");
        Track t1 = new Track(1, "Title1", a1, new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);
        Track t2 = new Track(2, "Title2", a2, new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);
        Track t3 = new Track(3, "Title3", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f );

        Predicate<Track> artistPred = new ArtistPredicate(a1);
        assertFalse(artistPred.test(t2));

    }

    @Test
    private void TestArtistPredicateSetTRUE() {
        Artist a1 = new Artist(1, "test1");
        Artist a2 = new Artist(2, "test2");
        Track t1 = new Track(1, "Title1", a1, new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);
        Track t2 = new Track(2, "Title2", a2, new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);
        Track t3 = new Track(3, "Title3", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);

        Set<Artist> aSet = new HashSet<>();
        aSet.add(a1);
        aSet.add(a2);
        Predicate<Track> artistPred = new ArtistPredicate(aSet);
        assertTrue(artistPred.test(t1));
        assertTrue(artistPred.test(t2));

    }

    @Test
    private void TestArtistPredicateSetFALSE() {
        Artist a1 = new Artist(1, "test1");
        Artist a2 = new Artist(2, "test2");
        Track t1 = new Track(1, "Title1", a1, new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);
        Track t2 = new Track(2, "Title2", a2, new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);
        Track t3 = new Track(3, "Title3", new Artist(), new Album(), "blacover", "blahash", 50, 0, new Date(), 120, 0.9f);

        Set<Artist> aSet = new HashSet<>();
        aSet.add(a1);
        aSet.add(a2);
        Predicate<Track> artistPred = new ArtistPredicate(aSet);
        assertFalse(artistPred.test(t3));
    }


}
