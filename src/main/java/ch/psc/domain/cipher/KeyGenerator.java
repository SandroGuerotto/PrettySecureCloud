package ch.psc.domain.cipher;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import ch.psc.domain.error.FatalImplementationException;


/**
 * https://www.veracode.com/blog/research/encryption-and-decryption-java-cryptography
 * There are 2 key based encryption algorithms: Symmetric and Asymmetric algorithms.
 * There are various cryptographic parameters which need to be configured correctly for a crypto-system to be secured; these include key size, mode of operation, padding scheme, IV, etc.
 * For symmetric encryption use the AES algorithm. For asymmetric encryption, use the RSA algorithm.
 * Use a transformation that fully specifies the algorithm name, mode and padding. Most providers default to the highly insecure ECB mode of operation, if not specified.
 * Always use an authenticated mode of operation, i.e. AEAD (for example GCM or CCM) for symmetric encryption. If you have to use an unauthenticated mode, use CBC or CTR along with MAC to authenticate the ciphertext, correct random IV and padding parameters.
 * Use authentication tag with at least 128 bits length in AEAD modes.
 * Make sure to use OAEPWith<digest>And<mgf>Padding for asymmetric encryption, where the digest is SHA1/SHA256/384/512. Use PKCS5Padding for symmetric encryption.
 * If using PDKDF for key generation or Password Based Encryption (PBE), make sure to use SHA2 algorithms, a salt value of at least 64 bits and iteration count of 10,000.
 * Key sizes: use AES 256 if you can, else 128 is secure enough for time being. For RSA use at least 2048, consider 4096 or longer for future proofing.
 * There is a limit on how much plaintext can be safely encrypted using a single (key/IV) pair in CBC and CTR modes.
 * The randomness source of an IV comes from the IvParameterSpec class and not from init methods of the Cipher class.
 *
 *         Generate an key using KeyGenerator + Initialize the provided keysize
 *          - Methode, welche prüft, ob algorithm und keyBites kompatibel sind?
 *          - allenfalls auch eine abstrakte klasse mit entsprechenden implementationen? analog zu cipher
 *          - To add to the complexity of a cipher, Initialization Vectors are used. Brauchen wir auch, vllt als Option?
 *          - to consider: https://alex-labs.com/reasonably-secure-way-store-secret-java/
 */
public class KeyGenerator {

  public static final String PUBLIC_KEY_POSTFIX = ".pub";

  public Map<String, Key> generateKey(int keyBits, String algorithm) throws FatalImplementationException {
    KeyType type = testKeyType(algorithm);
    Map<String, Key> keyChain = null;
    
    switch(type) {
      case SYMMETRIC:
        keyChain = generateSymmetricKey(keyBits, algorithm);
        break;
      case ASYMMETRIC:
        keyChain = generateAsymmetricKey(keyBits, algorithm);
        break;
      case NOT_SUPPORTED:
      default:
        throw new FatalImplementationException("Algorithm '" + algorithm + "' is not supported! Please check spelling.");
    }
    
    return keyChain;
  }
  
  protected Map<String, Key> generateAsymmetricKey(int keyBits, String algorithm) throws FatalImplementationException {
    Map<String, Key> keyChain = new HashMap<>();
    
    try {
      java.security.KeyPairGenerator keyGenerator = java.security.KeyPairGenerator.getInstance(algorithm);
      keyGenerator.initialize(keyBits);
      KeyPair generated = keyGenerator.generateKeyPair();
      keyChain.put(algorithm, new Key(generated.getPrivate()));
      keyChain.put(algorithm + PUBLIC_KEY_POSTFIX, new Key(generated.getPublic()));
    } catch (NoSuchAlgorithmException e) {
      throw new FatalImplementationException("Transformation '" + algorithm + "' does not exist!", e);
    }
    
    return keyChain;
  }

  protected Map<String, Key> generateSymmetricKey(int keyBits, String algorithm) throws FatalImplementationException {
    Map<String, Key> keyChain = new HashMap<>();

    try {
      javax.crypto.KeyGenerator keyGenerator = javax.crypto.KeyGenerator.getInstance(algorithm);
      keyGenerator.init(keyBits); // 128, 192 or 256 for AES
      SecretKey secretKey = keyGenerator.generateKey();
      Key key = new Key(secretKey);
      keyChain.put(algorithm, key);
    } catch (NoSuchAlgorithmException e) {
      throw new FatalImplementationException("Transformation '" + algorithm + "' does not exist!", e);
    }

    return keyChain;
  }
  
  /**
   * Tests whether the algorithm is symmetric or asymmetric.
   * Try/Catch has to be used, since Java does not seem to provide a test class for that.
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
        //TODO when decided for a logging framework: log algorithm
      }
    }
    
    return type;
  }
  
  private enum KeyType {
    SYMMETRIC, ASYMMETRIC, NOT_SUPPORTED;
  }

}
