package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.util.function.Predicate;

/**
 * Created by David on 28.11.2017.
 */
public class MoodPredicate implements Predicate<Track>{

    private float arousal, valence;

    public MoodPredicate(Radio radio){

        this.valence = this.arousal = 0f;

        if(radio.getArousal()!=null)
            this.arousal = radio.getArousal();
        if(radio.getValence()!=null)
            this.valence = (radio.getValence()+1)/2; //Needed to map the -1..1 interval to the Spotify 0..1 interval
    }

    @Override
    public boolean test(Track track) {
        return Math.abs(track.getArousal()-this.arousal)<=0.05 &&
                Math.abs(track.getValence()-this.valence)<=0.05;
    }
}
