package de.reactivejukebox.logger;

public class LoggerImp implements Logger {

    LoggerImp() {
        printHead();
    }

    void printHead() {
        String msg = "";
        // TODO implement
        writeEntry(msg);
    }

    public void writeEntry(final Entry en) {
        writeEntry(en.getLogString());
    }

    private void writeEntry(final String msg) {
        // TODO log4j call
        // TODO handle errors
        System.out.println(msg);
    }
}
