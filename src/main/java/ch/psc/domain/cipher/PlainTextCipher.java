package ch.psc.domain.cipher;

import javax.crypto.NullCipher;
import ch.psc.exceptions.FatalImplementationException;

/**
 * <b>Do not use this for secure Data!</b>
 * This Cipher is a dummy implementation and for easy testing purposes. It will not encrypt any data and return plain text.
 * @author Lorenz
 *
 */
public class PlainTextCipher extends Cipher {
  
  private static final String TYPE = "Plain Text";
  private static final String ALGORYTHM = "NONE";
  private static final String TRANSFORMATION = "";
  private static final SecurityLevel SECURITY_LEVEL = SecurityLevel.none;

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public SecurityLevel getSecurityLevel() {
    return SECURITY_LEVEL;
  }

  @Override
  public String getAlgorythm() {
    return ALGORYTHM;
  }

  @Override
  public String getTransformation() {
    return TRANSFORMATION;
  }
  
  @Override
  protected javax.crypto.Cipher getCipher() throws FatalImplementationException {
    javax.crypto.Cipher cipher = new NullCipher();
    return cipher;
  }

}
