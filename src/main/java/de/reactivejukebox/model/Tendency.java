package de.reactivejukebox.model;

import java.io.Serializable;


/**
 * The Tendency class is a model for a tendency for the tracks suggested by the jukebox
 */
public class Tendency implements Serializable {

    private int id; //global tendency id
    private User user;
    private Radio radio;
    private float preferredDynamics;
    private int preferredSpeed;
    private int preferredPeriodStart;
    private int preferredPeriodEnd;

    public Tendency() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Radio getRadio() {
        return radio;
    }

    public void setRadio(Radio radio) {
        this.radio = radio;
    }

    public float getPreferredDynamics() {
        return preferredDynamics;
    }

    public void setPreferredDynamics(float preferredDynamics) {
        this.preferredDynamics = preferredDynamics;
    }

    public int getPreferredSpeed() {
        return preferredSpeed;
    }

    public void setPreferredSpeed(int preferredSpeed) {
        this.preferredSpeed = preferredSpeed;
    }

    public int getPreferredPeriodStart() {
        return preferredPeriodStart;
    }

    public void setPreferredPeriodStart(int preferredPeriodStart) {
        this.preferredPeriodStart = preferredPeriodStart;
    }

    public int getPreferredPeriodEnd() {
        return preferredPeriodEnd;
    }

    public void setPreferredPeriodEnd(int preferredPeriodEnd) {
        this.preferredPeriodEnd = preferredPeriodEnd;
    }

    /**
     * Creates a matching TendencyPlain object.
     *
     * @return the matching TendencyPlain object with the same attributes as this Tendency object
     */
    public TendencyPlain getPlainObject() {
        TendencyPlain plainTendency = new TendencyPlain();
        plainTendency.setUserId(this.getUser().getId());
        plainTendency.setRadioId(this.getRadio().getId());
        plainTendency.setId(this.getId());

        plainTendency.setPreferredDynamics(this.getPreferredDynamics());
        plainTendency.setPreferredPeriodEnd(this.getPreferredPeriodEnd());
        plainTendency.setPreferredPeriodStart(this.getPreferredPeriodStart());
        plainTendency.setPreferredSpeed(this.getPreferredSpeed());

        return plainTendency;
    }

}

