package ch.psc.domain.cipher;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import ch.psc.exceptions.FatalImplementationException;

/**
 * Following the Factory Pattern, this class handles the creation of required
 * {@link java.security.Key} for the selected cipher. In particular, this class assess the cipher
 * algorithm and creates either a symmetric key or an asymmetric key pair.
 *
 * @author Tristan, Lorenz
 */
public class KeyGenerator {

  public static final String PUBLIC_KEY_POSTFIX = ".pub";

  /**
   * Generates and returns the required {@link java.security.Key} based on provided parameters
   *
   * @param keyBits length of key in bits
   * @param algorithm selected algorithm, e.g. "AES"
   * @return Map<String, Key> containing 1 or 2 keys
   * @throws FatalImplementationException
   */
  public Map<String, Key> generateKey(int keyBits, String algorithm)
      throws FatalImplementationException {
    KeyType type = testKeyType(algorithm);
    Map<String, Key> keyChain = null;

    switch (type) {
      case SYMMETRIC:
        keyChain = generateSymmetricKey(keyBits, algorithm);
        break;
      case ASYMMETRIC:
        keyChain = generateAsymmetricKey(keyBits, algorithm);
        break;
      case NOT_SUPPORTED:
      default:
        throw new FatalImplementationException(
            "Algorithm '" + algorithm + "' is not supported! Please check spelling.");
    }

    return keyChain;
  }

  protected Map<String, Key> generateAsymmetricKey(int keyBits, String algorithm)
      throws FatalImplementationException {
    Map<String, Key> keyChain = new HashMap<>();

    try {
      java.security.KeyPairGenerator keyGenerator =
          java.security.KeyPairGenerator.getInstance(algorithm);
      keyGenerator.initialize(keyBits);
      KeyPair generated = keyGenerator.generateKeyPair();
      keyChain.put(algorithm, new Key(generated.getPrivate()));
      keyChain.put(algorithm + PUBLIC_KEY_POSTFIX, new Key(generated.getPublic()));
    } catch (NoSuchAlgorithmException e) {
      throw new FatalImplementationException("Transformation '" + algorithm + "' does not exist!",
          e);
    }

    return keyChain;
  }

  protected Map<String, Key> generateSymmetricKey(int keyBits, String algorithm)
      throws FatalImplementationException {
    Map<String, Key> keyChain = new HashMap<>();

    try {
      javax.crypto.KeyGenerator keyGenerator = javax.crypto.KeyGenerator.getInstance(algorithm);
      keyGenerator.init(keyBits); // 128, 192 or 256 for AES
      SecretKey secretKey = keyGenerator.generateKey();
      Key key = new Key(secretKey);
      keyChain.put(algorithm, key);
    } catch (NoSuchAlgorithmException e) {
      throw new FatalImplementationException("Transformation '" + algorithm + "' does not exist!",
          e);
    }

    return keyChain;
  }

  /**
   * Tests whether the algorithm is symmetric or asymmetric. Try/Catch has to be used, since Java
   * does not seem to provide a test class for that.
   *
   * @param algorithm String representation of the algorithm.
   * @return {@link KeyType#NOT_SUPPORTED} if no supported Key Generator was found.
   */
  private KeyType testKeyType(String algorithm) {
    KeyType type = null;

    try {
      javax.crypto.KeyGenerator.getInstance(algorithm);
      type = KeyType.SYMMETRIC;
    } catch (NoSuchAlgorithmException e) {
      try {
        java.security.KeyPairGenerator.getInstance(algorithm);
        type = KeyType.ASYMMETRIC;
      } catch (NoSuchAlgorithmException e1) {
        type = KeyType.NOT_SUPPORTED;
        // TODO when decided for a logging framework: log algorithm
      }
    }

    return type;
  }

  private enum KeyType {
    SYMMETRIC, ASYMMETRIC, NOT_SUPPORTED;
  }

}
