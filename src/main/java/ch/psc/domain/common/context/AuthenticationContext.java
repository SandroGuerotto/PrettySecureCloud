package ch.psc.domain.common.context;

import ch.psc.domain.user.AuthenticationService;

/**
 * @author Sandro
 */
public class AuthenticationContext {

    private static final ThreadLocal<AuthenticationService> authServiceContext = new ThreadLocal<>();

    public static AuthenticationService getAuthService() {
        return authServiceContext.get();
    }

    public static void setAuthService(AuthenticationService authenticationService) {
        authServiceContext.set(authenticationService);
    }


}
