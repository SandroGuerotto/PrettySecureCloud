package ch.psc.domain.common.context;

import ch.psc.domain.user.User;

/**
 * Encapsulates information about a user using psc-application.
 * If a user is logged in / authorized, the user in {@link #userContext } is set to the current logged-in user.
 * On logout or close of the application user in {@link #userContext } is set to null.
 * Only one user can exist per instance.
 *
 * @author SandroGuerotto
 */
public class UserContext {

    private static final ThreadLocal<User> userContext = new ThreadLocal<>();

    /**
     * Get authorized / logged-in user.
     *
     * @return authorized user
     */
    public static User getAuthorizedUser() {
        return userContext.get();
    }

    /**
     * Set authorized / logged-in user.
     *
     * @param user authorized user
     */
    public static void setAuthorizedUser(User user) {
        userContext.set(user);
    }


}
