package de.reactivejukebox.api;

import de.reactivejukebox.user.UserData;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Created by lang on 6/6/17.
 */
public class BasicSecurityContext implements SecurityContext {
    private final UserData user;
    private final boolean secure;

    public BasicSecurityContext(UserData user, boolean secure) {
        this.user = user;
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return user.getUsername();
            }
        };
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }

    @Override
    public boolean isSecure() { return secure; }

    @Override
    public boolean isUserInRole(String role) {
        return user.getRoles().contains(role);
    }
}
