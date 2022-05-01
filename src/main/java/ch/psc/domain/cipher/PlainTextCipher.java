package ch.psc.domain.cipher;

import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.NullCipher;
import ch.psc.exceptions.FatalImplementationException;

/**
 * <b>Do not use this for secure Data!</b>
 * This Cipher is a dummy implementation and for easy testing purposes. It will not encrypt any data and return plain text.
 *
 * @author Lorenz
 *
 */
public class PlainTextCipher extends PscCipher {

  private static final String ALGORITHM = "PLAIN_TEXT";
  private static final String TRANSFORMATION = "";
  private static final SecurityLevel SECURITY_LEVEL = SecurityLevel.none;

  @Override
  public SecurityLevel getSecurityLevel() {
    return SECURITY_LEVEL;
  }

  @Override
  public String getAlgorithm() {
    return ALGORITHM;
  }

  @Override
  public String getTransformation() {
    return TRANSFORMATION;
  }

  @Override
  protected Cipher getCipher() throws FatalImplementationException {
    javax.crypto.Cipher cipher = new NullCipher();
    return cipher;
  }
  
  @Override
  public int getKeyBits() {
    return 16;
  }

  @Override
  public Map<String, Key> generateKey() throws ch.psc.domain.error.FatalImplementationException {
    return new HashMap<>();
  }

}
