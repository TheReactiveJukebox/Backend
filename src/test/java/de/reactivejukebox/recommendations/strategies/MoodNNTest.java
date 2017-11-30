package de.reactivejukebox.recommendations.strategies;

import com.sun.org.apache.regexp.internal.RE;
import de.reactivejukebox.model.*;
import de.reactivejukebox.recommendations.RecommendationStrategy;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

/**
 * Created by David on 28.11.2017.
 */
public class MoodNNTest {

    private final float[] AROUSALS = {0.4f,0.4f,-0.4f,-0.4f};
    private final float[] VALENCES = {-0.4f,0.4f,0.4f,-0.4f};

    @BeforeClass
    private void setUpModel() throws SQLException{
        Tracks tracks = new Tracks();

        Model m = mock(Model.class);
        HistoryEntries h = mock(HistoryEntries.class);
        Mockito.when(m.getTracks()).thenReturn(tracks);
        Mockito.when(m.getHistoryEntries()).thenReturn(h);
        Mockito.when(h.getListByRadioId(1)).thenReturn(new ArrayList<>());

        setModelInstance(m);

        Random rng = new Random(0);
        Album stub = new Album();

        Artist checker;
        float a,v;
        for (int i=0;i<4;i++){
            checker = new Artist(i+1,"Artist");
            for(int j=0;j<100;j++){
                a = AROUSALS[i]-0.05f+(rng.nextFloat()*0.1f);
                v = VALENCES[i]-0.05f+(rng.nextFloat()*0.1f);
                Model.getInstance().getTracks().put(i*100+j,new Track(i*100+j,"",checker,stub,"","",
                        0,0,null,0f,0f,v,a));
            }
        }

        ArrayList<HistoryEntry> history = new ArrayList<>();
        for(int i=0;i<80;i++)
        history.add(new HistoryEntry(i,Model.getInstance().getTracks().get(i),new Radio(),new User(),new Timestamp(1))); //Add track to history

        Mockito.when(h.getListByRadioId(2)).thenReturn(history);
    }

    @Test
    public void TestNNModel() throws Exception{

        Radio radio = new Radio();
        radio.setId(1);
        radio.getStartTracks().add(new Track(888,"",null, null,"","",
                0,0,null,0f,0f,-0.4f,0.4f));

        RecommendationStrategy strat= new MoodNN(radio,new ArrayList<Track>(),20);
        List<Track> result = strat.getRecommendations();
        Reporter.log("Size: "+result.size(),true);
        float prevScore=-1;
        for (Track e:result){
            Reporter.log(e.toString(),true);
            assertTrue(e.getArtist().getId()==1);
            Reporter.log("Prev: "+prevScore+", Current: "+ e.getRecScore(),true);
            assertTrue(prevScore<=e.getRecScore());
            prevScore = e.getRecScore();
        }

        radio.getStartTracks().clear();
        radio.getStartTracks().add(new Track(888,"",null, null,"","",
                0,0,null,0f,0f,0.4f,0.4f));

        result = strat.getRecommendations();
        Reporter.log("Size: "+result.size(),true);
        prevScore=-1;
        for (Track e:result){
            Reporter.log(e.toString(),true);
            assertTrue(e.getArtist().getId()==2);
            Reporter.log("Prev: "+prevScore+", Current: "+ e.getRecScore(),true);
            assertTrue(prevScore<=e.getRecScore());
            prevScore = e.getRecScore();
        }

        radio.getStartTracks().clear();
        radio.getStartTracks().add(new Track(888,"",null, null,"","",
                0,0,null,0f,0f,0.4f,-0.4f));

        result = strat.getRecommendations();
        Reporter.log("Size: "+result.size(),true);
        prevScore=-1;
        for (Track e:result){
            Reporter.log(e.toString(),true);
            assertTrue(e.getArtist().getId()==3);
            Reporter.log("Prev: "+prevScore+", Current: "+ e.getRecScore(),true);
            assertTrue(prevScore<=e.getRecScore());
            prevScore = e.getRecScore();
        }

        radio.getStartTracks().clear();
        radio.getStartTracks().add(new Track(888,"",null, null,"","",
                0,0,null,0f,0f,-0.4f,-0.4f));

        result = strat.getRecommendations();
        Reporter.log("Size: "+result.size(),true);
        prevScore=-1;
        for (Track e:result){
            assertTrue(e.getArtist().getId()==4);
            assertTrue(prevScore<=e.getRecScore());
            prevScore = e.getRecScore();
        }


        radio.getStartTracks().add(new Track(888,"",null, null,"","",
                0,0,null,0f,0f,-0.4f,0.4f));

        result = strat.getRecommendations();
        Reporter.log("Size: "+result.size(),true);
        prevScore=-1;
        for (Track e:result){
            assertTrue(e.getArtist().getId()==1 || e.getArtist().getId() == 4);
            assertTrue(prevScore<=e.getRecScore());
            prevScore = e.getRecScore();
        }

    }

    @Test
    public void TestNNHistory() throws Exception{
        Radio radio = new Radio();
        radio.setId(2);
        radio.getStartTracks().add(new Track(888,"",null, null,"","",
                0,0,null,0f,0f,-0.4f,0.4f));

        RecommendationStrategy strat= new MoodNN(radio,new ArrayList<Track>(),20);
        List<Track> result = strat.getRecommendations();
        for (Track e:result) {
            Reporter.log(e.toString(), true);
            assertTrue(e.getId()>=80);
        }
    }

    private void setModelInstance(Model m) {
        try {
            Field instance = Model.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, m);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Could not set instance field of model class using reflection!");
            Assert.fail();
        }
    }
}
