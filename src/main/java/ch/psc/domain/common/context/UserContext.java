package ch.psc.domain.common.context;

import ch.psc.domain.user.User;

/**
 * @author Sandro
 */
public class UserContext {

    private static final ThreadLocal<User> userContext = new ThreadLocal<>();

    public static User getAuthorizedUser() {
        return userContext.get();
    }

    public static void setAuthorizedUser(User user) {
        userContext.set(user);
    }


}
