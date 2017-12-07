package de.reactivejukebox.recommendations.filters;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;

import java.util.function.Predicate;

/**
 * Created by David on 28.11.2017.
 */
public class MoodPredicate implements Predicate<Track>{

    private float arousal, valence, window;

    public MoodPredicate(Radio radio){
        this(radio.getArousal(),radio.getValence());
    }

    public MoodPredicate(float arousal, float valence){
        this(arousal,valence, 0.1f);
    }

    public MoodPredicate(float arousal, float valence, float window){
        this.arousal = arousal;
        this.valence = valence;
        this.window = window;
    }

    @Override
    public boolean test(Track track) {
        return Math.abs(track.getArousal()-this.arousal)<=this.window &&
                Math.abs(track.getValence()-this.valence)<=this.window;
    }
}
