package ch.psc.domain.user;

import ch.psc.exceptions.AuthenticationException;

/**
 * @author Sandro
 */
public interface AuthService {

    User authenticate(String email, String pwd) throws AuthenticationException;

    User signup(User user) throws AuthenticationException;

    User update(User user);
}
