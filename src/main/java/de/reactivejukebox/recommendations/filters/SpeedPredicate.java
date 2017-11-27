package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.util.function.Predicate;

public class SpeedPredicate implements Predicate<Track>

    {
        private Float minSpeed;
        private Float maxSpeed;


    public SpeedPredicate(Radio radio) {
        minSpeed = radio.getMinSpeed();
        maxSpeed = radio.getMaxSpeed();
    }

        @Override
        public boolean test(Track track) {
        boolean result = true;
        if( minSpeed != null){
            result = result & minSpeed < track.getSpeed();
        }
        if (maxSpeed != null){
            result = result & maxSpeed > track.getSpeed();
        }
        return  result;
    }

}
