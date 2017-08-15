package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.util.function.Predicate;

/**
 * Returns TRUE if the Track was published within the given timespan
 */
public class PublishedPredicate implements Predicate<Track> {
    private int startYear;
    private int endYear;

    public PublishedPredicate(Radio radio) {
        startYear = radio.getStartYear();
        endYear = radio.getEndYear();
    }

    @Override
    public boolean test(Track track) {
        return startYear <= track.getDate().getYear() && track.getDate().getYear() <= endYear;
    }

}
