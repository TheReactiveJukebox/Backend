package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import de.reactivejukebox.recommendations.Recommendations;
import de.reactivejukebox.recommendations.filters.HistoryPredicate;
import de.reactivejukebox.recommendations.filters.SpeedPredicate;

import java.util.ArrayList;
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

    private static final float DefaultWindow = 5f;
    private static final float MaxWindow = 10f;
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

    public SpeedNN(Radio radio, Collection<Track> upcoming, int resultCount, float minSpeed, float maxSpeed){
        this.radio = radio;
        this.upcoming = upcoming;
        this.resultCount = resultCount;
        this.window = (maxSpeed-minSpeed)/2;
        this.speeds = new HashSet<>();
        this.speeds.add((minSpeed+maxSpeed)/2);
    }

    public SpeedNN(Radio radio, Collection<Track> upcoming, int resultCount, Collection<Float> speeds,float window){
        this.speeds = new HashSet<>(speeds);
        this.radio = radio;
        this.upcoming = upcoming;
        this.resultCount = resultCount;
        this.window = window;
        //If a speed filter is set in addition to selected Songs
        if(radio.getMinSpeed()!=null || radio.getMaxSpeed()!=null){
            if(radio.getMinSpeed()!=null && radio.getMaxSpeed()!=null){
                this.window = Math.min(Math.max((radio.getMaxSpeed()-radio.getMinSpeed())/2, 1),MaxWindow);
                this.speeds.add((radio.getMinSpeed()+radio.getMaxSpeed())/2);
            }
            //Remove speeds outside the filter range
            for (Float f:this.speeds){
                if (radio.getMinSpeed()!=null && f<radio.getMinSpeed())
                    this.speeds.remove(f);
                if (radio.getMaxSpeed()!=null && f>radio.getMaxSpeed())
                    this.speeds.remove(f);
            }
        }
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

    private List<Float> getScores(List<Track> recommendations){
        return recommendations.stream().map(Track::getSpeed).map(this::calcDistance).map(this::score).collect(Collectors.toList());
    }

    private float score(float distance){//1 when exact speed match, 0 when at the edge of the filter window, quadratic decay
        return (float)Math.pow((window - distance)/window,2);
    }

    @Override
    public Recommendations getRecommendations() {
        List<Track> tracks = speeds.stream().map(caster).flatMap(this::nearestNeighbors).distinct()
                .sorted(((o1, o2) -> Float.compare(calcDistance(o1.getSpeed()),calcDistance(o2.getSpeed()))))
                .skip(this.speeds.size())//Remove if Algorithm needs to return the selected start tracks as recommendations
                .limit(resultCount)
                .collect(Collectors.toList());
        List<Float> score = this.getScores(tracks);
        return new Recommendations(tracks,score);
    }
}
