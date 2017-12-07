package de.reactivejukebox.logger;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;

public class LoggerProviderTest {
    @Test
    public void testGetLogger() {
        Logger l = LoggerProvider.getLogger();

        // Assert
        assertNotNull(l);
    }
}
