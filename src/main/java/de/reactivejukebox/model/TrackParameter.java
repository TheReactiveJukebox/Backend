package de.reactivejukebox.model;

public class TrackParameter {
    int oldestTrack;
    float minSpeed;
    float maxSpeed;

    public int getOldestTrack() {
        return oldestTrack;
    }

    public void setOldestTrack(int oldestTrack) {
        this.oldestTrack = oldestTrack;
    }

    public float getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(float minSpeed) {
        this.minSpeed = minSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
