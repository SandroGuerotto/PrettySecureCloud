package ch.psc.domain.cipher;


/**
 * Simple class which is being used to transfer the relevant {@link java.security.Key} stored in the
 * {@link Key} class
 *
 * @author Tristan, Lorenz
 */
public class Key {

  private final java.security.Key key;

  /**
   * Constructor requires {@link java.security.Key} or any subclass.
   *
   * @param secretKey {@link java.security.Key}
   */
  public Key(java.security.Key secretKey) {
    this.key = secretKey;
  }

  /**
   * @return {@link java.security.Key}
   */
  public java.security.Key getKey() {
    return key;
  }

  /**
   * @return String containing the algorithm reference, e.g. "AES"
   */
  public String getType() {
    return key.getAlgorithm();
  }

}
