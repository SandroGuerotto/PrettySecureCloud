package ch.psc.domain.common.context;

import ch.psc.domain.user.AuthService;

/**
 * @author Sandro
 */
public class AuthenticationContext {

    private static final ThreadLocal<AuthService> authServiceContext = new ThreadLocal<>();

    public static AuthService getAuthService() {
        return authServiceContext.get();
    }

    public static void setAuthService(AuthService authService) {
        authServiceContext.set(authService);
    }


}
