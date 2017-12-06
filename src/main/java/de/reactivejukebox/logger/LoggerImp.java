package de.reactivejukebox.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LoggerImp implements Logger {
    private final static String FILE_PATH = "logger/event-log.csv";
    private final static Character DELIMITER = ';';

    private static Logger instance;

    private FileWriter fw;
    private BufferedWriter bw;

    LoggerImp() {

        try {
            // open file in append mode
            fw = new FileWriter(FILE_PATH, true);

            bw = new BufferedWriter(fw);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            e.printStackTrace();
        }

    }

    }

    public void writeEntry(final Entry en) throws Entry.IsInvalid {
        if (!en.isValid()) {
            throw new EntryIsInvalid();
        }

        StringBuilder msg = new StringBuilder();
        for (String colValue: en.getEntry()) {
            msg.append(colValue);
            msg.append(DELIMITER);
        }

        writeEntry(msg.toString());
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
