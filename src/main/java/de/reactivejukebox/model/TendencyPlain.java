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
    private boolean older;
    private boolean newer;
    private String moreOfGenre;


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
}

