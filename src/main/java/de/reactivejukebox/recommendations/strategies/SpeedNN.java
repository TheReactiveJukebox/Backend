package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.filters.HistoryPredicate;
import de.reactivejukebox.recommendations.filters.SpeedPredicate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by David on 07.12.2017.
 */
public class SpeedNN implements RecommendationStrategy{

    private static final float DefaultWindow = 10f;
    private HashSet<Float> speeds;
    private Radio radio;
    private Collection<Track> upcoming;
    private int resultCount;
    private float window;

    private static Function<Float,Track> caster = new Function<Float, Track>() {
        @Override
        public Track apply(Float aFloat) {
            Track result = new Track();
            result.setSpeed(aFloat);
            return result;
        }
    };

    public SpeedNN(Radio radio, Collection<Track> upcoming, int resultCount){
       this(radio,upcoming,resultCount, radio.getStartTracks().stream().map(Track::getSpeed).collect(Collectors.toSet()));
    }

    public SpeedNN(Radio radio, Collection<Track> upcoming, int resultCount, Collection<Float> speeds){
        this(radio,upcoming,resultCount,speeds,DefaultWindow);
    }

    public SpeedNN(Radio radio, Collection<Track> upcoming, int resultCount, Collection<Float> speeds,float window){
        this.speeds = new HashSet<>(speeds);
        this.radio = radio;
        this.upcoming = upcoming;
        this.resultCount = resultCount;
        this.window = window;
    }

    public void addSpeed(float speed){
        this.speeds.add(speed);
    }

    public void clearSpeeds(){
        this.speeds.clear();
    }

    private float calcDistance(float speed){
       return this.speeds.stream().map(f->Math.abs(f-speed)).min(Float::compareTo).get();
    }

    private Stream<Track> nearestNeighbors(Track speed){
        return radio.filter(Model.getInstance().getTracks().stream())
                .filter(new SpeedPredicate(speed.getSpeed(),this.window))
                .filter(new HistoryPredicate(radio,upcoming));
    }

    @Override
    public List<Track> getRecommendations() {
        return speeds.stream().map(caster).flatMap(this::nearestNeighbors).distinct()
                .sorted(((o1, o2) -> Float.compare(calcDistance(o1.getSpeed()),calcDistance(o2.getSpeed()))))
                .limit(resultCount)
                .collect(Collectors.toList());
    }
}
