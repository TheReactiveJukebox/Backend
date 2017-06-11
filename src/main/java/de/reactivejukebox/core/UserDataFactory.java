package de.reactivejukebox.core;

import de.reactivejukebox.user.UserData;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;


/**
 * makes Generates UserData for "@Context UserData user"
 */
public class UserDataFactory implements Factory<UserData> {
    private final ContainerRequestContext context;

    @Inject
    public UserDataFactory(ContainerRequestContext context) {
        this.context = context;
    }

    @Override
    public UserData provide() {
        return (UserData) context.getProperty("UserData");
    }

    @Override
    public void dispose(UserData t) {
    }
}
