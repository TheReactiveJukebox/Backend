package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.*;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import java.lang.reflect.Field;
import java.sql.SQLException;
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
        Mockito.when(m.getTracks()).thenReturn(tracks);
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
