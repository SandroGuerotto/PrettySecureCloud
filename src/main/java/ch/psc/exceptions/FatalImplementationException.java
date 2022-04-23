package ch.psc.exceptions;

import java.security.GeneralSecurityException;

public class FatalImplementationException extends Exception {
  
  /**
   * 
   */
  private static final long serialVersionUID = -7412356196801128451L;

  public FatalImplementationException(String message) {
    super(message);
  }
  
  public FatalImplementationException(String message, GeneralSecurityException parent) {
    super(message, parent);
  }

}
