package ch.psc.exceptions;

public class KeyDeSerializationException extends AuthenticationException {

  /**
   * 
   */
  private static final long serialVersionUID = 8689618983130647418L;

  public KeyDeSerializationException(String message) {
    super(message);
  }

  public KeyDeSerializationException(String message, Exception cause) {
    super(message, cause);
  }
}
