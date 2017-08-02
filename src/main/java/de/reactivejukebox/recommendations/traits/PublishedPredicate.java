package de.reactivejukebox.recommendations.traits;

import de.reactivejukebox.model.*;
import java.util.function.Predicate;


public class PublishedPredicate implements Predicate<Track> {
    private int startYear ;
    private int endYear;

    public PublishedPredicate(Radio radio){
        startYear = radio.getStartYear();
        endYear = radio.getEndYear();
    }

    @Override
    public boolean test(Track track) {
        return startYear <= track.getDate().getYear() && track.getDate().getYear() <= endYear;
    }

}
