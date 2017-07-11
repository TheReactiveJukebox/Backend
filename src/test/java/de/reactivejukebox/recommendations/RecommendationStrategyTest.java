package de.reactivejukebox.recommendations;

import de.reactivejukebox.recommendations.strategies.SameArtistGreatestHits;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RecommendationStrategyTest {

    @Test
    public void testCreateStrategy() throws Exception {
        RecommendationStrategyFactory rf = new RecommendationStrategyFactory();
        RecommendationStrategy rs = rf.createStrategy("sagh");
        assertTrue(rs instanceof SameArtistGreatestHits);
    }

}