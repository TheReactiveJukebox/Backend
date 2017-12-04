package de.reactivejukebox.logger;

public class NullLogger implements Logger {
    private static Logger instance;

    public static Logger getInstance() {
        if (NullLogger.instance == null) {
            NullLogger.instance = new NullLogger();
        }
        return NullLogger.instance;
    }

    @Override
    public void writeEntry(Entry e) throws EntryIsInvalid {
        // do nothing
    }
}
