package de.reactivejukebox;

public class JukeboxConfig {
    public static final String SPOTIFY_CLIENT_ID = getEnvironmentVariable("SPOTIFY_CLIENT_ID");
    public static final String SPOTIFY_CLIENT_SECRET = getEnvironmentVariable("SPOTIFY_CLIENT_SECRET");
    public static String SPOTIFY_AUTH_TOKEN;

    private static String getEnvironmentVariable(String variable) {
        String value = System.getenv(variable);
        if (value == null) {
            throw new RuntimeException("Environment variable " + variable + " not set.");
        }
        return value;
    }
}
