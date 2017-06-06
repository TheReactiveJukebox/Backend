package de.reactivejukebox.core;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by lang on 6/6/17.
 */
public class ReactiveJukebox extends ResourceConfig {
    public ReactiveJukebox(){
        packages(true, "de.reactivejukebox.api");
    }
}
