package ch.psc.domain.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import ch.psc.domain.file.EncryptionState;
import ch.psc.domain.file.PscFile;
import ch.psc.exceptions.FatalImplementationException;

/**
 * Abstract base class for PSC ciphers.<br />
 * This class uses {@link javax.crypto.Cipher} to encrypt/decrypt {@link PscFile}s. Classes
 * inheriting from this class need to provide the specifics of which encryption algorithms will be
 * used.
 *
 * @author Tristan, Lorenz
 */
public abstract class PscCipher {

  /**
   * Always provide a public empty constructor
   */
  public PscCipher() {
    super();
  }

  private ExecutorService executor = Executors.newFixedThreadPool(5);

  /**
   * Which level of security is provided by this Cipher implementation? Provides additional
   * information to the end-user.
   *
   * @return Level of security
   */
  public abstract SecurityLevel getSecurityLevel();

  /**
   * Returns the amount of bits required for the relevant algorithm.
   *
   * @return int number of bits
   */
  public abstract int getKeyBits();

  /**
   * The name of the cryptographic algorithm (e.g.: AES, RSA). For a list of available
   * implementations in Java see: <a href=
   * 'https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#cipher-algorithm-names'>Algorithm
   * Names</a>
   *
   * @return Name of the cryptographic algorithm
   */
  public abstract String getAlgorithm();

  /**
   * Provides the Algorithm, Mode and Padding in the format "Algo/Mode/Padding". See following
   * sections for a List of combinations:
   * <ul>
   * <li><a href=
   * 'https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#cipher-algorithm-names'>Algorithm</a></li>
   * <li><a href=
   * 'https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#cipher-algorithm-modes'>Mode</a></li>
   * <li><a href=
   * 'https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#cipher-algorithm-paddings'>Padding</a></li>
   * </ul>
   * Please consider not every combination may make sense!
   *
   * @return The specific transformation of this cryptographic algorithm.
   */
  public abstract String getTransformation();

  /**
   * Returns the length of the used nonce/initialization vector.
   *
   * @return 0 if no nonce is used
   */
  public abstract int getNonceLength();

  /**
   * Creates the {@link AlgorithmParameterSpec} required for this encryption method. Default
   * implementation returns <code>null</code>, which is handled as if no
   * {@link AlgorithmParameterSpec} are needed. This means every Algorithm, which needs for example
   * a nonce/initializationVector needs to overwrite this method!
   *
   * @param file <b>WARNING:</b> This method may have side effects on this parameter! If a new
   *        nonce/initializationVector is generated for this file, the method is expected to set the
   *        new value.
   * @return Specifications implementing the {@link AlgorithmParameterSpec} interface.
   */
  public AlgorithmParameterSpec getAlgorithmSpecification(PscFile file) {
    return null;
  }

  /**
   * Encrypts a list of {@link PscFile}s. The encryption is done asynchronously and the method
   * returns as soon as all threads are started.
   *
   * @param key The {@link Key} to use for this encryption.
   * @param files A List of {@link PscFile}s to encrypt.
   * @return A List of {@link Future}s. Each {@link PscFile} is encrypted asynchronous, the result
   *         may be requested through the Future.
   */
  public List<Future<PscFile>> encrypt(Key key, List<PscFile> files) {
    java.security.Key secretKey = key.getKey();
    List<Future<PscFile>> encryptedFutures = new LinkedList<>();

    files.forEach(file -> {
      FutureTask<PscFile> encryptionTask = new FutureTask<PscFile>(() -> encrypt(file, secretKey));
      encryptedFutures.add(encryptionTask);
      executor.execute(encryptionTask);
    });

    return encryptedFutures;
  }

  /**
   * Decrypts a list of {@link PscFile}s. The decryption is done asynchronously and the method
   * returns as soon as all threads are started.
   *
   * @param key The {@link Key} to use for this decryption.
   * @param files A List of {@link PscFile}s to decrypt.
   * @return A List of {@link Future}s. Each {@link PscFile} is decrypted asynchronous, the result
   *         may be requested through the Future.
   */
  public List<Future<PscFile>> decrypt(Key key, List<PscFile> files) {
    java.security.Key secretKey = key.getKey();
    List<Future<PscFile>> decryptedFiles = new LinkedList<>();

    files.forEach(file -> {
      FutureTask<PscFile> decryptionTask = new FutureTask<PscFile>(() -> decrypt(file, secretKey));
      decryptedFiles.add(decryptionTask);
      executor.execute(decryptionTask);
    });

    return decryptedFiles;
  }

  /**
   * Creates a new {@link javax.crypto.Cipher}, initializes it and uses the Cipher to encrypt the
   * {@link PscFile}.
   *
   * @param file The {@link PscFile} to encrypt.
   * @param key The {@link Key} to use for encrypting.
   * @return A new {@link PscFile} with identical metadata but encrypted data.
   * @throws InvalidKeyException If the given key is inappropriate for initializing this cipher.
   * @throws FatalImplementationException If this Cipher is fundamentally wrong implemented (e.g.
   *         non-existing Transformation).
   * @throws InvalidAlgorithmParameterException If the given algorithm parameters are inappropriate
   *         for this cipher. Check the Method {@link PscCipher#getAlgorithmSpecification(PscFile)}!
   */
  protected PscFile encrypt(PscFile file, java.security.Key key)
      throws InvalidKeyException, FatalImplementationException, InvalidAlgorithmParameterException {
    javax.crypto.Cipher cipher = getCipher();
    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, getAlgorithmSpecification(file));
    byte[] unencryptedData = file.getData();
    PscFile encryptedFile = new PscFile(file.getName(), file.getPath());
    encryptedFile.setNonce(file.getNonce());
    performCipher(unencryptedData, encryptedFile, cipher);
    encryptedFile.setEncryptionState(EncryptionState.ENCRYPTED);

    return encryptedFile;
  }

  /**
   * Creates a new {@link javax.crypto.Cipher}, initializes it and uses the Cipher to decrypt the
   * {@link PscFile}.
   *
   * @param file The {@link PscFile} to decrypt.
   * @param key The {@link Key} to use for decrypting.
   * @return A new {@link PscFile} with identical metadata but decrypted data.
   * @throws InvalidKeyException If the given key is inappropriate for initializing this cipher.
   * @throws FatalImplementationException If this Cipher is fundamentally wrong implemented (e.g.
   *         non-existing Transformation).
   * @throws InvalidAlgorithmParameterException
   */
  protected PscFile decrypt(PscFile file, java.security.Key key)
      throws InvalidKeyException, FatalImplementationException, InvalidAlgorithmParameterException {
    javax.crypto.Cipher cipher = getCipher();
    cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, getAlgorithmSpecification(file));

    byte[] encryptedData = file.getData();
    PscFile decryptedFile = new PscFile(file.getName(), file.getPath());
    performCipher(encryptedData, decryptedFile, cipher);
    decryptedFile.setEncryptionState(EncryptionState.DECRYPTED);

    return decryptedFile;
  }

  /**
   * Creates a new instance of the {@link javax.crypto.Cipher} for this implementation.
   *
   * @return An uninitialized {@link PscCipher} instance.
   * @throws FatalImplementationException If the provided Transformation is null, empty,
   *         ill-formatted or no implementation is provided by the platform.
   */
  protected javax.crypto.Cipher getCipher() throws FatalImplementationException {
    javax.crypto.Cipher cipher;
    try {
      cipher = javax.crypto.Cipher.getInstance(getTransformation());
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new FatalImplementationException(
          "Transformation '" + getTransformation() + "' does not exist!", e);
    }
    return cipher;
  }

  /**
   * Executes the provided Cipher and sets the transformed data to the new {@link PscFile}.
   *
   * @param newFile <b>Will be manipulated</b> The {@link PscFile} where to set transformed data.
   * @param cipher The {@link javax.crypto.Cipher} to execute
   * @param oldData Data to be transformed by the Cipher.
   * @throws FatalImplementationException Will be thrown if the implementation of this Class has
   *         fundamental flaws.
   */
  protected void performCipher(byte[] oldData, PscFile newFile, javax.crypto.Cipher cipher)
      throws FatalImplementationException {
    try {
      newFile.setData(cipher.doFinal(oldData));
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      throw new FatalImplementationException(
          "Transformation '" + getTransformation() + "' is illegal", e);
    }
  }

  /**
   * Method used to find the {@link Key} used to encrypt data.
   *
   * @param keyChain Map with Key-name and {@link Key}
   * @return the {@link Key} with {@link PscCipher#getAlgorithm()} as map-key
   */
  public Key findEncryptionKey(Map<String, Key> keyChain) {
    return keyChain.get(getAlgorithm());
  }

  /**
   * Method used to find the {@link Key} used to decrypt data. <b>Needs to be overwritten by
   * Public-/Privatekey implementations!</b>
   *
   * @param keyChain Map with Key-name and {@link Key}
   * @return the {@link Key} with {@link PscCipher#getAlgorithm()} as map-key.
   */
  public Key findDecryptionKey(Map<String, Key> keyChain) {
    return keyChain.get(getAlgorithm());
  }

  /**
   * Creates a {@link Key} from a {@link Key} which contains {@link SecretKey}s in order to work
   * with {@link javax.crypto.Cipher}s.
   *
   * @return {@link Key} which can be used with {@link javax.crypto.Cipher}s.
   */
  public Map<String, Key> generateKey() throws FatalImplementationException {
    KeyGenerator keyGenerator = new KeyGenerator();
    return keyGenerator.generateKey(getKeyBits(), getAlgorithm());
  }

}
