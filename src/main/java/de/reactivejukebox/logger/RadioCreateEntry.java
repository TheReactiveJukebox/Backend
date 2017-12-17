package de.reactivejukebox.logger;

import de.reactivejukebox.model.Radio;
import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.User;

import java.util.List;
import java.util.StringJoiner;

public class RadioCreateEntry extends Entry {
    public RadioCreateEntry(User user, Radio radio) {
        super(Event.RADIO_START, user);
        setValue(EntryCol.RADIO, radio.getId());
        setValue(EntryCol.ALGORITHM, radio.getAlgorithm().toString());
        setValue(EntryCol.AROUSAL, radio.getArousal());
        setValue(EntryCol.VALENCE, radio.getValence());
        setValue(EntryCol.DYNAMIC, radio.getDynamic());
        setValue(EntryCol.YEAR_START, radio.getStartYear());
        setValue(EntryCol.YEAR_END, radio.getEndYear());
        setValue(EntryCol.SPEED_MIN, radio.getMinSpeed());
        setValue(EntryCol.SPEED_MAX, radio.getMaxSpeed());

        StringJoiner songValue = new StringJoiner(",");
        List<Track> startTracks = radio.getStartTracks();
        if (startTracks != null) {
            for (Track song : startTracks) {
                songValue.add(String.valueOf(song.getId()));
            }
        }
        setValue(EntryCol.SONG, songValue.toString());

        String[] genre = radio.getGenres();
        if (genre != null) {
            String genreValue = String.join(",", genre);
            setValue(EntryCol.GENRE, genreValue);
        }

        setValue(EntryCol.JSON, radio.getPlainObject());
    }
}
