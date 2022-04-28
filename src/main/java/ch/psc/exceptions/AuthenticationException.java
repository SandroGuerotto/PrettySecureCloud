package ch.psc.exceptions;

/**
 * @author Sandro Guerotto
 */
public class AuthenticationException extends Throwable{

    public AuthenticationException(String message) {
        super(message);
    }
}
