package de.reactivejukebox.core;

import de.reactivejukebox.model.User;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;


/**
 * Generates User for "@Context User user"
 */
public class UserDataFactory implements Factory<User> {
    private final ContainerRequestContext context;

    @Inject
    public UserDataFactory(ContainerRequestContext context) {
        this.context = context;
    }

    @Override
    public User provide() {
        return (User) context.getProperty("User");
    }

    @Override
    public void dispose(User t) {
    }
}
