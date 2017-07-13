package de.reactivejukebox.model;

import java.io.Serializable;

/**
 * Created by Ben on 13.07.2017.
 */
public class Feedback implements Serializable {

    private int id; //global feedback id
    private User user;
    private Radio radio;
    private boolean songLiked;
    private boolean songDisliked;
    private boolean artistLiked;
    private boolean artistDisliked;
    private boolean speedLiked;
    private boolean speedDisliked;
    private boolean genreLiked;
    private boolean genreDisliked;
    private boolean dynamicsLiked;
    private boolean dynamicsDisliked;
    private boolean periodLiked;
    private boolean periodDisliked;
    private boolean moodLiked;
    private boolean moodDisliked;

    public Feedback(int id, User user, Radio radio, boolean songLiked, boolean songDisliked, boolean artistLiked, boolean artistDisliked, boolean speedLiked, boolean speedDisliked, boolean genreLiked, boolean genreDisliked, boolean dynamicsLiked, boolean dynamicsDisliked, boolean periodLiked, boolean periodDisliked, boolean moodLiked, boolean moodDisliked) {
        this.id = id;
        this.user = user;
        this.radio = radio;
        this.songLiked = songLiked;
        this.songDisliked = songDisliked;
        this.artistLiked = artistLiked;
        this.artistDisliked = artistDisliked;
        this.speedLiked = speedLiked;
        this.speedDisliked = speedDisliked;
        this.genreLiked = genreLiked;
        this.genreDisliked = genreDisliked;
        this.dynamicsLiked = dynamicsLiked;
        this.dynamicsDisliked = dynamicsDisliked;
        this.periodLiked = periodLiked;
        this.periodDisliked = periodDisliked;
        this.moodLiked = moodLiked;
        this.moodDisliked = moodDisliked;
    }

    public Feedback() {
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

    public boolean isSongLiked() {
        return songLiked;
    }

    public void setSongLiked(boolean songLiked) {
        this.songLiked = songLiked;
    }

    public boolean isSongDisliked() {
        return songDisliked;
    }

    public void setSongDisliked(boolean songDisliked) {
        this.songDisliked = songDisliked;
    }

    public boolean isArtistLiked() {
        return artistLiked;
    }

    public void setArtistLiked(boolean artistLiked) {
        this.artistLiked = artistLiked;
    }

    public boolean isArtistDisliked() {
        return artistDisliked;
    }

    public void setArtistDisliked(boolean artistDisliked) {
        this.artistDisliked = artistDisliked;
    }

    public boolean isSpeedLiked() {
        return speedLiked;
    }

    public void setSpeedLiked(boolean speedLiked) {
        this.speedLiked = speedLiked;
    }

    public boolean isSpeedDisliked() {
        return speedDisliked;
    }

    public void setSpeedDisliked(boolean speedDisliked) {
        this.speedDisliked = speedDisliked;
    }

    public boolean isGenreLiked() {
        return genreLiked;
    }

    public void setGenreLiked(boolean genreLiked) {
        this.genreLiked = genreLiked;
    }

    public boolean isGenreDisliked() {
        return genreDisliked;
    }

    public void setGenreDisliked(boolean genreDisliked) {
        this.genreDisliked = genreDisliked;
    }

    public boolean isDynamicsLiked() {
        return dynamicsLiked;
    }

    public void setDynamicsLiked(boolean dynamicsLiked) {
        this.dynamicsLiked = dynamicsLiked;
    }

    public boolean isDynamicsDisliked() {
        return dynamicsDisliked;
    }

    public void setDynamicsDisliked(boolean dynamicsDisliked) {
        this.dynamicsDisliked = dynamicsDisliked;
    }

    public boolean isPeriodLiked() {
        return periodLiked;
    }

    public void setPeriodLiked(boolean periodLiked) {
        this.periodLiked = periodLiked;
    }

    public boolean isPeriodDisliked() {
        return periodDisliked;
    }

    public void setPeriodDisliked(boolean periodDisliked) {
        this.periodDisliked = periodDisliked;
    }

    public boolean isMoodLiked() {
        return moodLiked;
    }

    public void setMoodLiked(boolean moodLiked) {
        this.moodLiked = moodLiked;
    }

    public boolean isMoodDisliked() {
        return moodDisliked;
    }

    public void setMoodDisliked(boolean moodDisliked) {
        this.moodDisliked = moodDisliked;
    }
}
