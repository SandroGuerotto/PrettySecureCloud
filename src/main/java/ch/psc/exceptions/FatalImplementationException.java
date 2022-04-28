package ch.psc.exceptions;

public class FatalImplementationException extends Exception {
  
  /**
   * 
   */
  private static final long serialVersionUID = -7412356196801128451L;

  public FatalImplementationException(String message) {
    super(message);
  }
  
  public FatalImplementationException(String message, Throwable parent) {
    super(message, parent);
  }

}
