package de.reactivejukebox.logger;

public class LoggerProvider {
    private static Logger instance;

    public static Logger getLogger() {
        if (instance == null) {
            instance = new LoggerImp();
        }
        return instance;
    }
}
