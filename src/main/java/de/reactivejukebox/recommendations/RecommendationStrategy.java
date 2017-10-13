package de.reactivejukebox.recommendations;

import de.reactivejukebox.model.Track;

import java.util.List;

public interface RecommendationStrategy {
    List<Track> getRecommendations();
}
