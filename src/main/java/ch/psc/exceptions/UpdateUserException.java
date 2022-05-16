package ch.psc.exceptions;

import java.io.Serial;

/**
 * @author Sandro
 */
public class UpdateUserException extends Exception {

  @Serial
  private static final long serialVersionUID = -7412996196801148451L;

  public UpdateUserException(String message, Throwable e) {
    super(message, e);
  }

  public UpdateUserException(String message) {
    super(message);
  }
}
