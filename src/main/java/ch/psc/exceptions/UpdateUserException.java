package ch.psc.exceptions;

/**
 * @author Sandro
 */
public class UpdateUserException extends Throwable {
    public UpdateUserException(String message, Throwable e) {
        super(message);
    }

    public UpdateUserException(String message) {
        super(message);
    }
}
