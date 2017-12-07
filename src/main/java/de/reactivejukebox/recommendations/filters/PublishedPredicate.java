package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.util.Calendar;
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
        if (radio.getStartYear() == null){
            startYear = 0;
        }else{
            startYear = radio.getStartYear();
        }
        if (radio.getEndYear() == null){
            Calendar now = Calendar.getInstance();   // Gets the current date and time
            endYear = now.get(Calendar.YEAR);       // The current year
        }else{
            endYear = radio.getEndYear();
        }
        startDate = new GregorianCalendar(startYear,0,1).getTime();
        endDate = new GregorianCalendar(endYear,11,31).getTime();
    }

    @Override
    public boolean test(Track track) {
		if (track.getReleaseDate() == null) {
			return false;
		} else {
			return track.getReleaseDate().after(startDate) && track.getReleaseDate().before(endDate);
		}
    }

}
