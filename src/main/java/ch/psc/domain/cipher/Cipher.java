package ch.psc.domain.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import ch.psc.domain.file.File;
import ch.psc.exceptions.FatalImplementationException;

/**
 * Abstract base class for PSC ciphers.<br />
 * This class uses {@link javax.crypto.Cipher} to encrypt/decrypt {@link File}s.
 * Classes inheriting from this class need to provide the specifics of which encryption algorithms will be used.
 * 
 * @author Tristan, Lorenz
 *
 */
public abstract class Cipher {
  
  private ExecutorService executor = Executors.newFixedThreadPool(5);
  
  /**
   * Which level of security is provided by this Cipher implementation?
   * Provides additional information to the end-user.
   * 
   * @return Level of security
   */
  public abstract SecurityLevel getSecurityLevel();
  
  /**
   * The name of the cryptographic algorithm (e.g.: AES, RSA).
   * For a list of available implementations in Java see: <a href='https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#cipher-algorithm-names'>Algorithm Names</a>
   * 
   * @return Name of the cryptographic algorithm
   */
  public abstract String getAlgorythm();
  
  /**
   * Provides the Algorithm, Mode and Padding in the format "Algo/Mode/Padding". 
   * See following sections for a List of combinations:
   * <ul><li><a href='https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#cipher-algorithm-names'>Algorithm</a></li>
   * <li><a href='https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#cipher-algorithm-modes'>Mode</a></li>
   * <li><a href='https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#cipher-algorithm-paddings'>Padding</a></li></ul>
   * Please consider not every combination may make sense!
   * 
   * @return The specific transformation of this cryptographic algorithm.
   */
  public abstract String getTransformation();
  
  /**
   * Encrypts a list of {@link File}s. The encryption is done asynchronously and the method returns as soon as all threads are started.
   * 
   * @param key The {@link Key} to use for this encryption.
   * @param files A List of {@link File}s to encrypt.
   * @return A List of {@link Future}s. Each {@link File} is encrypted asynchronous, the result may be requested through the Future.
   */
  public List<Future<File>> encrypt(Key key, List<File> files) {
    SecretKey secretKey = generateKey(key);
    List<Future<File>> encryptedFutures = new LinkedList<>();
    
    files.forEach( file -> {
      FutureTask<File> encryptionTask = new FutureTask<File>(() -> encrypt(file, secretKey));
      encryptedFutures.add(encryptionTask);
      executor.execute(encryptionTask);
    });
    
    return encryptedFutures;
  }
  
  /**
   * Decrypts a list of {@link File}s. The decryption is done asynchronously and the method returns as soon as all threads are started.
   * 
   * @param key The {@link Key} to use for this decryption.
   * @param files A List of {@link File}s to decrypt.
   * @return A List of {@link Future}s. Each {@link File} is decrypted asynchronous, the result may be requested through the Future.
   */
  public List<Future<File>> decrypt(Key key, List<File> files) {
    SecretKey secretKey = generateKey(key);
    List<Future<File>> decryptedFiles = new LinkedList<>();
    
    files.forEach( file -> {
      FutureTask<File> decryptionTask = new FutureTask<File>(() -> decrypt(file, secretKey));
      decryptedFiles.add(decryptionTask);
      executor.execute(decryptionTask);
    });
      
    return decryptedFiles;
  }
  
  /**
   * Creates a new {@link javax.crypto.Cipher}, initializes it and uses the Cipher to encrypt the {@link File}.
   * 
   * @param file The {@link File} to encrypt.
   * @param key The {@link Key} to use for encrypting.
   * @return A new {@link File} with identical metadata but encrypted data.
   * @throws InvalidKeyException If the given key is inappropriate for initializing this cipher.
   * @throws FatalImplementationException If this Cipher is fundamentally wrong implemented (e.g. non-existing Transformation).
   * @throws InvalidAlgorithmParameterException If the given algorithm parameters are inappropriate for this cipher. Check the Method {@link #getAlgorithmSpecification()}!
   */
  protected File encrypt(File file, SecretKey key) throws InvalidKeyException, FatalImplementationException, InvalidAlgorithmParameterException {
    javax.crypto.Cipher cipher = getCipher();
    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
    
    byte[] unencryptedData = file.getData();
    File encryptedFile = new File();
    encryptedFile.setName(file.getName());
    encryptedFile.setPath(file.getPath());
    performCipher(unencryptedData, encryptedFile, cipher);
    encryptedFile.setEncryptionState(EncryptionState.ENCRYPTED);
    
    return encryptedFile;
  }
  
  /**
   * Creates a new {@link javax.crypto.Cipher}, initializes it and uses the Cipher to decrypt the {@link File}.
   * 
   * @param file The {@link File} to decrypt.
   * @param key The {@link Key} to use for decrypting.
   * @return A new {@link File} with identical metadata but decrypted data.
   * @throws InvalidKeyException If the given key is inappropriate for initializing this cipher.
   * @throws FatalImplementationException If this Cipher is fundamentally wrong implemented (e.g. non-existing Transformation).
   * @throws InvalidAlgorithmParameterException 
   */
  protected File decrypt(File file, SecretKey key) throws InvalidKeyException, FatalImplementationException, InvalidAlgorithmParameterException {
    javax.crypto.Cipher cipher = getCipher();
    cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
    
    byte[] encryptedData = file.getData();
    File decryptedFile = new File();
    decryptedFile.setName(file.getName());
    decryptedFile.setPath(file.getPath());
    performCipher(encryptedData, decryptedFile, cipher);
    decryptedFile.setEncryptionState(EncryptionState.DECRYPTED);
    
    return decryptedFile;
  }
  
  /**
   * Creates a new instance of the {@link javax.crypto.Cipher} for this implementation.
   * 
   * @return An uninitialized {@link Cipher} instance.
   * @throws FatalImplementationException If the provided Transformation is null, empty, ill-formatted or no implementation is provided by the platform.
   */
  protected javax.crypto.Cipher getCipher() throws FatalImplementationException {
    javax.crypto.Cipher cipher;
    try {
      cipher = javax.crypto.Cipher.getInstance(getTransformation());
    }
    catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new FatalImplementationException("Transformation '" + getTransformation() + "' does not exist!", e);
    }
    return cipher;
  }
  
  /**
   * Executes the provided Cipher and sets the transformed data to the new File.
   *  
   * @param newFile <b>Will be manipulated</b> The {@link File} where to set transformed data.
   * @param cipher The {@link javax.crypto.Cipher} to execute
   * @param oldData Data to be transformed by the Cipher.
   * @throws FatalImplementationException Will be thrown if the implementation of this Class has fundamental flaws.
   */
  protected void performCipher(byte[] oldData, File newFile, javax.crypto.Cipher cipher) throws FatalImplementationException {
    try {
      newFile.setData(cipher.doFinal(oldData));
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      throw new FatalImplementationException("Transformation '" + getTransformation() +"' is illegal", e);
    }
  }
  
  /**
   * Creates a {@link SecretKey} from a {@link Key}. {@link SecretKey}s are required for working with {@link javax.crypto.Cipher}s.
   * 
   * @param key {@link Key} containing the secret of this algorithm.
   * @return {@link SecretKey} which can be used with {@link javax.crypto.Cipher}s.
   */
  protected SecretKey generateKey(Key key) {
    SecretKey secretKey = new SecretKeySpec(key.getKey(), getAlgorythm());
    return secretKey;
  }
}
