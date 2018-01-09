package de.reactivejukebox.model;

public class SpeedFeedback {

        private int fSpeed;
        private int feedback;

        public SpeedFeedback(){}

        public SpeedFeedback(int fSpeed, int feedback){
            this.fSpeed = fSpeed;
            this.feedback = feedback;
        }

        public int getFSpeed() {
            return fSpeed;
        }

        public void setFSpeed(int artist) {
            this.fSpeed = fSpeed;
        }

        public int getFeedback() {
            return feedback;
        }

        public void setFeedback(int feedback) {
            this.feedback = feedback;
        }
}
