package de.reactivejukebox.core;

import de.reactivejukebox.model.User;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;


/**
 * javax.ws.rs.Application Class
 */
public class ReactiveJukebox extends ResourceConfig {
    public ReactiveJukebox() {
        packages(true, "de.reactivejukebox.api");
        register(CORSResponseFilter.class);
        register(AuthenticationFilter.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(UserDataFactory.class)
                        .to(User.class)
                        .in(RequestScoped.class);
            }
        });
    }
}
