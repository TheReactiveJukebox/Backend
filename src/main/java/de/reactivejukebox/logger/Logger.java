package de.reactivejukebox.logger;

public interface Logger {
    void writeEntry(Entry e) throws EntryIsInvalid;
}
