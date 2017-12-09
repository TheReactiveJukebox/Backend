package de.reactivejukebox.recommendations.strategies;

import de.reactivejukebox.model.Track;
import de.reactivejukebox.model.UserProfile;
import de.reactivejukebox.recommendations.Recommendations;

import java.util.ArrayList;
import java.util.Map;

public class HybridMod {
    private final float likeMod = 1.5f;
    private final float disLikeMod = -0.5f;
    private final float artistMod = 1.25f;
    private final float disArtistMod = -0.75f;
    private final float albumMod = 1.25f;
    private final float disAlbumMod = -0.75f;

    private UserProfile userProfile;

    public HybridMod(UserProfile userProfile){
        this.userProfile = userProfile;
    }

    public Recommendations modifyRanking (Map<Track,Float> input){
        ArrayList<Track> tracks = new ArrayList<>();
        ArrayList<Float> scores = new ArrayList<>();
        ArrayList<Float> mods = new ArrayList<>();

        tracks.addAll(input.keySet());
        for (Track t: tracks) {
            Integer tId = t.getId();

            Float tMod = likeMod * userProfile.getTrackFeedback(tId) + disLikeMod * userProfile.getTrackFeedback(tId);
            if (tMod > 0) mods.add(tMod);
            Float arMod = artistMod * userProfile.getArtistFeedback(tId) + disArtistMod * userProfile.getArtistFeedback(tId);
            if (arMod > 0) mods.add(arMod);
            Float alMod = albumMod * userProfile.getAlbumFeedback(tId) + disAlbumMod * userProfile.getAlbumFeedback(tId);
            if (alMod > 0) mods.add(alMod);

            Float value = input.get(t);
            for (Float f : mods){
                value *= f;
            }
            scores.add(value);
        }
        return new Recommendations(tracks, scores);
    }
}
