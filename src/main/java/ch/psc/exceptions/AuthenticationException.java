package ch.psc.exceptions;

import java.io.Serial;

/**
 * @author Sandro Guerotto
 */
public class AuthenticationException extends Exception {

    @Serial
    private static final long serialVersionUID = -7412356196801148451L;
    public AuthenticationException(String message) {
        super(message);
    }
    public AuthenticationException(String message, Exception cause) {
      super(message, cause);
    }
}
