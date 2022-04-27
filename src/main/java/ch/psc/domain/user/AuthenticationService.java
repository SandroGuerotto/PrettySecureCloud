package ch.psc.domain.user;

import ch.psc.exceptions.AuthenticationException;

/**
 * @author Sandro
 */
public interface AuthenticationService {
    /**
     *
     * @param email
     * @param pwd
     * @return
     * @throws AuthenticationException
     */
    User authenticate(String email, String pwd) throws AuthenticationException;

    /**
     * 
     * @param user
     * @return
     * @throws AuthenticationException
     */
    User signup(User user) throws AuthenticationException;

    User update(User user) throws Exception;
}
