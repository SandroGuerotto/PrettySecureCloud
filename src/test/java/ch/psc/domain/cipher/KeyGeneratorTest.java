package ch.psc.domain.cipher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.psc.exceptions.FatalImplementationException;

class KeyGeneratorTest {

  KeyGenerator keyGenerator;

  @BeforeEach
  public void beforeEach() {
    keyGenerator = new KeyGenerator();
  }

  @Test
  public void aes128Test() throws FatalImplementationException {
    Map<String, Key> keyChain = keyGenerator.generateKey(128, "AES");
    Key key = keyChain.get("AES");
    assertEquals("AES", key.getType());
    assertEquals(128 / 8, key.getKey().getEncoded().length);
  }

  @Test
  public void aes192Test() throws FatalImplementationException {
    Map<String, Key> keyChain = keyGenerator.generateKey(192, "AES");
    Key key = keyChain.get("AES");
    assertEquals("AES", key.getType());
    assertEquals(192 / 8, key.getKey().getEncoded().length);
  }

  @Test
  public void aes256Test() throws FatalImplementationException {
    Map<String, Key> keyChain = keyGenerator.generateKey(256, "AES");
    Key key = keyChain.get("AES");
    assertEquals("AES", key.getType());
    assertEquals(256 / 8, key.getKey().getEncoded().length);
  }

  @Test
  public void rsa1024Test() throws FatalImplementationException {
    Map<String, Key> keyChain = keyGenerator.generateKey(1024, "RSA");
    Key pub = keyChain.get("RSA" + KeyGenerator.PUBLIC_KEY_POSTFIX);
    Key priv = keyChain.get("RSA");
    assertEquals("RSA", pub.getType());
    assertEquals(1024, ((RSAPublicKey) pub.getKey()).getModulus().bitLength());
    assertEquals("RSA", priv.getType());
    assertEquals(1024, ((RSAPrivateKey) priv.getKey()).getModulus().bitLength());
  }

  @Test
  public void rsa2048Test() throws FatalImplementationException {
    Map<String, Key> keyChain = keyGenerator.generateKey(2048, "RSA");
    Key pub = keyChain.get("RSA" + KeyGenerator.PUBLIC_KEY_POSTFIX);
    Key priv = keyChain.get("RSA");
    assertEquals("RSA", pub.getType());
    assertEquals(2048, ((RSAPublicKey) pub.getKey()).getModulus().bitLength());
    assertEquals("RSA", priv.getType());
    assertEquals(2048, ((RSAPrivateKey) priv.getKey()).getModulus().bitLength());
  }

  @Test
  public void unsupportedAlgorithmTest() {
    assertThrows(FatalImplementationException.class,
        () -> keyGenerator.generateKey(1024, "NotSupportedAlgorithm"));
  }

}
