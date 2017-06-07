package de.reactivejukebox.api;

import de.reactivejukebox.user.UserData;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * Created by lang on 6/7/17.
 */
public class UserDataFactory implements Factory<UserData> {
    private final ContainerRequestContext context;

    @Inject
    public UserDataFactory(ContainerRequestContext context) {
        this.context = context;
    }

    @Override
    public UserData provide() {
        return (UserData)context.getProperty("UserData");
    }

    @Override
    public void dispose(UserData t) {}
}
