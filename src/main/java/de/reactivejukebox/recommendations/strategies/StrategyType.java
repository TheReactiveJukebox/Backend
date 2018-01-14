package de.reactivejukebox.recommendations.strategies;

public enum StrategyType {
    SAGH(2),
    RANDOM(1),
    HYBRID(0),
    MOOD(3),
    SPEED(3),
    SPOTIFY(4),
    FEATURES(3);

    StrategyType(float weight) {
        this.weight = weight;
    }

    private float weight;

    public float getWeight() {
        return weight;
    }
}
