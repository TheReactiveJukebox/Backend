package de.reactivejukebox.logger;

import org.apache.logging.log4j.LogManager;

public class LoggerImp implements Logger {

    LoggerImp() {
        writeEntry(Entry.getHeadEntry());
    }

    public void writeEntry(final Entry en) {
        writeEntry(en.getLogString());
    }

    private void writeEntry(final String msg) {
        LogManager.getLogger("studie").info(msg);
    }
}
