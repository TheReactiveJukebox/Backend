package de.reactivejukebox.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LoggerImp implements Logger {
    private final static boolean STATUS = true;
    private final static String FILE_PATH = "logger/event-log.csv";
    private final static Character DELIMITER = ';';

    private static LoggerImp instance;

    private FileWriter fw;
    private BufferedWriter bw;

    private LoggerImp() {
        if (!STATUS)
            return;

        try {
            // open file in append mode
            fw = new FileWriter(FILE_PATH, true);

            bw = new BufferedWriter(fw);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            e.printStackTrace();
        }

    }

    public synchronized static Logger getInstance() {
        if (LoggerImp.instance == null) {
            LoggerImp.instance = new LoggerImp();
        }
        return LoggerImp.instance;
    }

    public void writeEntry(final Entry en) throws EntryIsInvalid {
        if (!STATUS)
            return;

        if (!en.isValid()) {
            throw new EntryIsInvalid();
        }

        String msg = "";

        msg += en.getTime().toString() + DELIMITER;
        msg += en.getEvent().name() + DELIMITER;
        {
            Integer userId = en.getUserId();
            if (userId != null) {
                msg += userId.toString();
            }
            msg += DELIMITER;
        }
        // TODO so on

        writeEntry(msg);
    }

    private synchronized void writeEntry(final String msg) {
        assert (!msg.trim().equals(""));

        try {
            bw.write(msg);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            e.printStackTrace();
        }
    }
}
