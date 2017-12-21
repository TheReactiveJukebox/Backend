package de.reactivejukebox.recommendations.strategies;

public enum StrategyType {
    SAGH(5),
    RANDOM(1),
    HYBRID(0),
    MOOD(4),
    SPEED(4),
    FEATURES(6);

    StrategyType(float weight) {
        this.weight = weight;
    }

    private float weight;

    public float getWeight() {
        return weight;
    }
}
