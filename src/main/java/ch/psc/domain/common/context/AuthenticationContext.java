package ch.psc.domain.common.context;

import ch.psc.domain.user.AuthenticationService;

/**
 * Encapsulates authentication service in the psc-application.
 *
 * @author SandroGuerotto
 */
public class AuthenticationContext {

  private static final ThreadLocal<AuthenticationService> authServiceContext = new ThreadLocal<>();

  /**
   * Get used authentication service.
   *
   * @return authentication service
   */
  public static AuthenticationService getAuthService() {
    return authServiceContext.get();
  }

  /**
   * Sets authentication service.
   *
   * @param authenticationService authentication service
   */
  public static void setAuthService(AuthenticationService authenticationService) {
    authServiceContext.set(authenticationService);
  }


}
