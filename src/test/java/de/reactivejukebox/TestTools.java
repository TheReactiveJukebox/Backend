package de.reactivejukebox;

import de.reactivejukebox.model.Model;
import org.testng.Assert;

import java.lang.reflect.Field;

public class TestTools {

    public static void setModelInstance(Model m) {
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
