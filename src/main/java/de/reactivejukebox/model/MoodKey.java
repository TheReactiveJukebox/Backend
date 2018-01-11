package de.reactivejukebox.model;

public class MoodKey {

    private final int x;
    private final int y;


    public MoodKey(Float arousal, Float valence) {
        this.x = Math.round(arousal * 40);
        this.y = Math.round(valence * 40);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoodKey)) return false;
        MoodKey key = (MoodKey) o;
        return x == key.x && y == key.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

}