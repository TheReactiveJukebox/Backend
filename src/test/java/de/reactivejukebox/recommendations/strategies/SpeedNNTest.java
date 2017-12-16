package de.reactivejukebox.recommendations.strategies;

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
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;
/**
 * Created by David on 09.12.2017.
 */
public class SpeedNNTest {

    @BeforeClass
    private void setUpModel() throws SQLException {
        Tracks tracks = new Tracks();

        Model m = mock(Model.class);
        HistoryEntries h = mock(HistoryEntries.class);
        Mockito.when(m.getTracks()).thenReturn(tracks);
        Mockito.when(m.getHistoryEntries()).thenReturn(h);
        Mockito.when(h.getListByRadioId(1)).thenReturn(new ArrayList<>());

        setModelInstance(m);

        Random rng = new Random(0);

        for (int i=0;i<1000;i++){
            Model.getInstance().getTracks().put(i,new Track(i,"",null,null,"","",0,0,null,i*0.2f,0f));
        }

        ArrayList<HistoryEntry> history = new ArrayList<>();
        for(int i=0;i<80;i++)
            history.add(new HistoryEntry(i,Model.getInstance().getTracks().get(i),new Radio(),new User(),new Timestamp(1))); //Add track to history

        Mockito.when(h.getListByRadioId(2)).thenReturn(history);
    }

    @Test
    private void testConstructors() throws Exception{
        Radio radio= new Radio();
        radio.setId(1);

        float testSpeed=100f;

        radio.getStartTracks().add(new Track(1000,"",null,null,"","",0,0,null,testSpeed,0f));
        RecommendationStrategy strat = new SpeedNN(radio, new ArrayList<Track>(),20);



        Field windowField = SpeedNN.class.getDeclaredField("window");
        Field speedsField = SpeedNN.class.getDeclaredField("speeds");
        windowField.setAccessible(true);
        speedsField.setAccessible(true);
        float window = (float)windowField.get(strat);
        HashSet<Float> speeds = (HashSet<Float>)speedsField.get(strat);

        assertTrue(window == 5f);
        assertTrue(speeds.contains(testSpeed));

        List<Track> result = strat.getRecommendations();
        testResults(result,testSpeed,window);

        strat = new SpeedNN(new Radio(),new ArrayList<Track>(),20,90,120);
        window = (float)windowField.get(strat);
        speeds = (HashSet<Float>)speedsField.get(strat);
        assertTrue(window == 15f);
        assertTrue(speeds.contains(105f));

        List<Float> inputSpeeds = new ArrayList<>();
        Random rng = new Random(0);

        for (int i=0;i<10;i++){
            inputSpeeds.add(100*rng.nextFloat());
            inputSpeeds.add(50f);
        }

        strat = new SpeedNN(new Radio(),new ArrayList<Track>(),20,inputSpeeds);
        speeds = (HashSet<Float>)speedsField.get(strat);

        for (Float f:inputSpeeds){
            assertTrue(speeds.contains(f));
        }

    }

    private void testResults(List<Track> result,float testSpeed,float window){
        Reporter.log("Speed: "+testSpeed,true);
        float prev=-1;
        for (Track t:result){
            Reporter.log(t.toString(),true);
            assertTrue(Math.abs(t.getSpeed()-testSpeed)<=window);
            assertTrue(Math.abs(t.getSpeed()-testSpeed)>=prev);
            prev = Math.abs(t.getSpeed()-testSpeed);
        }
    }

    @Test
    private void testRecommendations() throws Exception{
        Radio radio= new Radio();
        radio.setId(1);

        float testSpeed=100f;

        radio.getStartTracks().add(new Track(1000,"",null,null,"","",0,0,null,testSpeed,0f));
        SpeedNN strat = new SpeedNN(radio, new ArrayList<Track>(),20);

        Field windowField = SpeedNN.class.getDeclaredField("window");
        Field speedsField = SpeedNN.class.getDeclaredField("speeds");
        windowField.setAccessible(true);
        speedsField.setAccessible(true);
        float window = (float)windowField.get(strat);
        HashSet<Float> speeds = (HashSet<Float>)speedsField.get(strat);

        assertTrue(window == 5f);
        assertTrue(speeds.contains(testSpeed));

        List<Track> result = strat.getRecommendations();
        testResults(result,testSpeed,window);

        Random rng = new Random(0);

        for (int i=0;i<20;i++){
            testSpeed = 200f*rng.nextFloat();
            strat.clearSpeeds();
            strat.addSpeed(testSpeed);
            result = strat.getRecommendations();
            testResults(result,testSpeed,window);
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

