package ch.psc.domain.user;

import ch.psc.exceptions.AuthenticationException;
import ch.psc.exceptions.UpdateUserException;

/**
 * A service for verifying credentials provided by a client.
 *
 * @author SandroGuerotto
 */
public interface AuthenticationService {
  /**
   * Authenticates entered credentials and returns user if authentication was successful
   *
   * @param email entered email for authentication
   * @param password entered password
   * @return authorized user
   * @throws AuthenticationException email or password wrong or user does not exist
   */
  User authenticate(String email, String password) throws AuthenticationException;

  /**
   * Register a new user to the psc-application.
   *
   * @param user user to sign up
   * @return signed-up user
   * @throws AuthenticationException sign-up failed
   */
  User signup(User user) throws AuthenticationException;

  /**
   * Updates current user.
   * 
   * @param user updated user
   * @return a new user with the updated data
   * @throws UpdateUserException update failed
   */
  User update(User user) throws UpdateUserException;
}
