package de.reactivejukebox.core;

import de.reactivejukebox.model.Model;
import de.reactivejukebox.model.User;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.concurrent.TimeUnit;


/**
 * javax.ws.rs.Application Class
 */
public class ReactiveJukebox extends ResourceConfig {
    private Model model;

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
        while (model == null){
            model = Model.getInstance();
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
