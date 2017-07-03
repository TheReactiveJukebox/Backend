package de.reactivejukebox.model;

public class Radio {
    
    private String id;
    private boolean random;
    private String refereceSongId;


    public Radio(String id, boolean random, String refereceSongId) {
        this.id = id;
        this.random = random;
        this.refereceSongId = refereceSongId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public String getRefereceSongId() {
        return refereceSongId;
    }

    public void setRefereceSongId(String refereceSongId) {
        this.refereceSongId = refereceSongId;
    }
}
