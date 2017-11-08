package de.reactivejukebox.model;

import java.io.Serializable;

/**
 * TendencyPlain is a tendency object containing only ids for its entities
 */
public class TendencyPlain implements Serializable {

    private int id; //global tendency id
    private int userId;
    private int radioId;
    private float preferredDynamics;
    private int preferredSpeed;
    private int preferredPeriodStart;
    private int preferredPeriodEnd;


    public TendencyPlain() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRadioId() {
        return radioId;
    }

    public void setRadioId(int radioId) {
        this.radioId = radioId;
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
}

