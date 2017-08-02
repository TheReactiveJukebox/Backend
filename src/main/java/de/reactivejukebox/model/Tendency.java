package de.reactivejukebox.model;

import java.io.Serializable;


/**
 * The Tendency class is a model for a tendency for the tracks suggested by the jukebox
 */
public class Tendency implements Serializable {

    private int id; //global tendency id
    private User user;
    private Radio radio;
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

    public boolean isStartOlder() { return startOlder; }

    public void setStartOlder(boolean startOlder) { this.startOlder = startOlder;  }

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

        plainTendency.setMoreDynamics(this.isMoreDynamics());
        plainTendency.setLessDynamics(this.isLessDynamics());
        plainTendency.setFaster(this.isFaster());
        plainTendency.setSlower(this.isSlower());
        plainTendency.setStartNewer(this.isStartNewer());
        plainTendency.setStartOlder(this.isStartOlder());
        plainTendency.setEndNewer(this.isEndNewer());
        plainTendency.setEndOlder(this.isEndOlder());
        plainTendency.setPreferredDynamics(this.getPreferredDynamics());
        plainTendency.setPreferredPeriodEnd(this.getPreferredPeriodEnd());
        plainTendency.setPreferredPeriodStart(this.getPreferredPeriodStart());
        plainTendency.setPreferredSpeed(this.getPreferredSpeed());

        plainTendency.setMoreOfGenre(this.getMoreOfGenre());

        return plainTendency;
    }

}

