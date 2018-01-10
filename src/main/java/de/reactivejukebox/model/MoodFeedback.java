package de.reactivejukebox.model;

public class MoodFeedback {

    private int fMood;
    private int feedback;

    public MoodFeedback(){}

    public MoodFeedback(int fMood, int feedback){
        this.fMood = fMood;
        this.feedback = feedback;
    }

    public int getfMood() {
        return fMood;
    }

    public void setfMood(int fMood) {
        this.fMood = fMood;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }
}