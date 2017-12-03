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

        this(radio.getArousal(),radio.getValence());

    }

    public MoodPredicate(float arousal,float valence){
        this.arousal = arousal;
        this.valence = (valence+1)/2;
    }

    @Override
    public boolean test(Track track) {
        return Math.abs(track.getArousal()-this.arousal)<=0.05 &&
                Math.abs(track.getValence()-this.valence)<=0.05;
    }
}
