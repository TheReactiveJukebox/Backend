package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.Predicate;

/**
 * Returns TRUE if the Track was published within the given timespan
 */
public class PublishedPredicate implements Predicate<Track> {
    private int startYear;
    private int endYear;
    private Date startDate;
    private Date endDate;

    public PublishedPredicate(Radio radio) {
        startYear = radio.getStartYear();
        startDate = new GregorianCalendar(startYear,0,1).getTime();
        endYear = radio.getEndYear();
        endDate = new GregorianCalendar(endYear,11,31).getTime();
    }

    @Override
    public boolean test(Track track) {
        return track.getReleaseDate().after(startDate) && track.getReleaseDate().before(endDate);
    }

}
