package de.reactivejukebox;

public class JukeboxConfig {
    public static final String spotifyClientId = getEnvironmentVariable("SPOTIFY_CLIENT_ID");
    public static final String spotifyClientSecret = getEnvironmentVariable("SPOTIFY_CLIENT_SECRET");
    public static String spotifyAuthToken;

    private static String getEnvironmentVariable(String variable) {
        String value = System.getenv(variable);
        if (value == null) {
            throw new RuntimeException("Environment variable " + variable + " not set.");
        }
        return value;
    }
}
