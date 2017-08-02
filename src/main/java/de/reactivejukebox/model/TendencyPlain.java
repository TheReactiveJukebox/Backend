package de.reactivejukebox.model;

import java.io.Serializable;

/**
 * TendencyPlain is a tendency object containing only ids for its entities
 */
public class TendencyPlain implements Serializable {

    private int id; //global tendency id
    private int userId;
    private int radioId;
    private boolean moreDynamics;
    private boolean lessDynamics;
    private boolean slower;
    private boolean faster;
    private boolean startOlder;
    private boolean startNewer;
    private boolean endOlder;
    private boolean endNewer;
    private String moreOfGenre;
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

    public boolean isMoreDynamics() {
        return moreDynamics;
    }

    public void setMoreDynamics(boolean moreDynamics) {
        this.moreDynamics = moreDynamics;
    }

    public boolean isLessDynamics() {
        return lessDynamics;
    }

    public void setLessDynamics(boolean lessDynamics) {
        this.lessDynamics = lessDynamics;
    }

    public boolean isSlower() {
        return slower;
    }

    public void setSlower(boolean slower) {
        this.slower = slower;
    }

    public boolean isFaster() {
        return faster;
    }

    public void setFaster(boolean faster) {
        this.faster = faster;
    }

    public boolean isStartOlder() {
        return startOlder;
    }

    public void setStartOlder(boolean startOlder) {
        this.startOlder = startOlder;
    }

    public boolean isStartNewer() { return startNewer; }

    public void setStartNewer(boolean startNewer) { this.startNewer = startNewer; }

    public boolean isEndOlder() { return endOlder; }

    public void setEndOlder(boolean endOlder) { this.endOlder = endOlder; }

    public boolean isEndNewer() { return endNewer; }

    public void setEndNewer(boolean endNewer) { this.endNewer = endNewer; }

    public String getMoreOfGenre() {
        return moreOfGenre;
    }

    public void setMoreOfGenre(String moreOfGenre) {
        this.moreOfGenre = moreOfGenre;
    }

    public float getPreferredDynamics() { return preferredDynamics; }

    public void setPreferredDynamics(float preferredDynamics) { this.preferredDynamics = preferredDynamics; }

    public int getPreferredSpeed() { return preferredSpeed; }

    public void setPreferredSpeed(int preferredSpeed) { this.preferredSpeed = preferredSpeed; }

    public int getPreferredPeriodStart() { return preferredPeriodStart; }

    public void setPreferredPeriodStart(int preferredPeriodStart) { this.preferredPeriodStart = preferredPeriodStart; }

    public int getPreferredPeriodEnd() { return preferredPeriodEnd;  }

    public void setPreferredPeriodEnd(int preferredPeriodEnd) { this.preferredPeriodEnd = preferredPeriodEnd; }
}

