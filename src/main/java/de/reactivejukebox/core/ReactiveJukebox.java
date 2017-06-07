package de.reactivejukebox.core;

import de.reactivejukebox.user.UserData;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by lang on 6/6/17.
 */
public class ReactiveJukebox extends ResourceConfig {
    public ReactiveJukebox(){
        packages(true, "de.reactivejukebox.api");
        register(CORSResponseFilter.class);
        register(new AbstractBinder(){
            @Override
            protected void configure() {
                bindFactory(UserDataFactory.class)
                        .to(UserData.class)
                        .in(RequestScoped.class);
            }
        });
    }
}
