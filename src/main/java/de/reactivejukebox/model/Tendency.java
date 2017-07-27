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
    private boolean older;
    private boolean newer;
    private String moreOfGenre;


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

    public boolean isOlder() {
        return older;
    }

    public void setOlder(boolean older) {
        this.older = older;
    }

    public boolean isNewer() {
        return newer;
    }

    public void setNewer(boolean newer) {
        this.newer = newer;
    }

    public String getMoreOfGenre() {
        return moreOfGenre;
    }

    public void setMoreOfGenre(String moreOfGenre) {
        this.moreOfGenre = moreOfGenre;
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
        plainTendency.setNewer(this.isNewer());
        plainTendency.setOlder(this.isOlder());
        plainTendency.setMoreOfGenre(this.getMoreOfGenre());
        return plainTendency;
    }

}

