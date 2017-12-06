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

    public void writeEntry(final Entry en) throws Entry.IsInvalid {
        writeEntry(en.getLogString());
    }

    private void writeEntry(final String msg) {
        // TODO log4j call
        System.out.println(msg);
    }
}
