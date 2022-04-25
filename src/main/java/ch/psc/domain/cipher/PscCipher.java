package ch.psc.domain.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.crypto.*;

import ch.psc.domain.file.PscFile;
import ch.psc.exceptions.FatalImplementationException;

/**
 * Abstract base class for PSC ciphers.<br />
 * This class uses {@link javax.crypto.Cipher} to encrypt/decrypt {@link PscFile}s.
 * Classes inheriting from this class need to provide the specifics of which encryption algorithms will be used.
 * 
 * @author Tristan, Lorenz
 *
 */
public abstract class PscCipher {
  
  private ExecutorService executor = Executors.newFixedThreadPool(5);
  
  /**
   * Which level of security is provided by this Cipher implementation?
   * Provides additional information to the end-user.
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
   * The name of the cryptographic algorithm (e.g.: AES, RSA).
   * For a list of available implementations in Java see: <a href='https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#cipher-algorithm-names'>Algorithm Names</a>
   * 
   * @return Name of the cryptographic algorithm
   */
  public abstract String getAlgorithm();

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
   * Encrypts a list of {@link PscFile}s. The encryption is done asynchronously and the method returns as soon as all threads are started.
   * 
   * @param key The {@link Key} to use for this encryption.
   * @param files A List of {@link PscFile}s to encrypt.
   * @return A List of {@link Future}s. Each {@link PscFile} is encrypted asynchronous, the result may be requested through the Future.
   */
  public List<Future<PscFile>> encrypt(Key key, List<PscFile> files) {
    SecretKey secretKey = key.getKey();
    List<Future<PscFile>> encryptedFutures = new LinkedList<>();
    
    files.forEach( file -> {
      FutureTask<PscFile> encryptionTask = new FutureTask<PscFile>(() -> encrypt(file, secretKey));
      encryptedFutures.add(encryptionTask);
      executor.execute(encryptionTask);
    });
    
    return encryptedFutures;
  }
  
  /**
   * Decrypts a list of {@link PscFile}s. The decryption is done asynchronously and the method returns as soon as all threads are started.
   * 
   * @param key The {@link Key} to use for this decryption.
   * @param files A List of {@link PscFile}s to decrypt.
   * @return A List of {@link Future}s. Each {@link PscFile} is decrypted asynchronous, the result may be requested through the Future.
   */
  public List<Future<PscFile>> decrypt(Key key, List<PscFile> files) {
    SecretKey secretKey = key.getKey();
    List<Future<PscFile>> decryptedFiles = new LinkedList<>();
    
    files.forEach( file -> {
      FutureTask<PscFile> decryptionTask = new FutureTask<PscFile>(() -> decrypt(file, secretKey));
      decryptedFiles.add(decryptionTask);
      executor.execute(decryptionTask);
    });

    return decryptedFiles;
  }
  
  /**
   * Creates a new {@link javax.crypto.Cipher}, initializes it and uses the Cipher to encrypt the {@link PscFile}.
   * 
   * @param file The {@link PscFile} to encrypt.
   * @param key The {@link Key} to use for encrypting.
   * @return A new {@link PscFile} with identical metadata but encrypted data.
   * @throws InvalidKeyException If the given key is inappropriate for initializing this cipher.
   * @throws FatalImplementationException If this Cipher is fundamentally wrong implemented (e.g. non-existing Transformation).
   * @throws InvalidAlgorithmParameterException If the given algorithm parameters are inappropriate for this cipher. Check the Method {@link #getAlgorithmSpecification()}!
   */
  protected PscFile encrypt(PscFile file, SecretKey key) throws InvalidKeyException, FatalImplementationException, InvalidAlgorithmParameterException {
    javax.crypto.Cipher cipher = getCipher();
    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
    
    byte[] unencryptedData = file.getData();
    PscFile encryptedFile = new PscFile();
    encryptedFile.setName(file.getName());
    encryptedFile.setPath(file.getPath());
    performCipher(unencryptedData, encryptedFile, cipher);
    encryptedFile.setEncryptionState(EncryptionState.ENCRYPTED);
    
    return encryptedFile;
  }
  
  /**
   * Creates a new {@link javax.crypto.Cipher}, initializes it and uses the Cipher to decrypt the {@link PscFile}.
   * 
   * @param file The {@link PscFile} to decrypt.
   * @param key The {@link Key} to use for decrypting.
   * @return A new {@link PscFile} with identical metadata but decrypted data.
   * @throws InvalidKeyException If the given key is inappropriate for initializing this cipher.
   * @throws FatalImplementationException If this Cipher is fundamentally wrong implemented (e.g. non-existing Transformation).
   * @throws InvalidAlgorithmParameterException 
   */
  protected PscFile decrypt(PscFile file, SecretKey key) throws InvalidKeyException, FatalImplementationException, InvalidAlgorithmParameterException {
    javax.crypto.Cipher cipher = getCipher();
    cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
    
    byte[] encryptedData = file.getData();
    PscFile decryptedFile = new PscFile();
    decryptedFile.setName(file.getName());
    decryptedFile.setPath(file.getPath());
    performCipher(encryptedData, decryptedFile, cipher);
    decryptedFile.setEncryptionState(EncryptionState.DECRYPTED);
    
    return decryptedFile;
  }
  
  /**
   * Creates a new instance of the {@link javax.crypto.Cipher} for this implementation.
   * 
   * @return An uninitialized {@link PscCipher} instance.
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
   * Executes the provided Cipher and sets the transformed data to the new {@link PscFile}.
   *  
   * @param newFile <b>Will be manipulated</b> The {@link PscFile} where to set transformed data.
   * @param cipher The {@link javax.crypto.Cipher} to execute
   * @param oldData Data to be transformed by the Cipher.
   * @throws FatalImplementationException Will be thrown if the implementation of this Class has fundamental flaws.
   */
  protected void performCipher(byte[] oldData, PscFile newFile, javax.crypto.Cipher cipher) throws FatalImplementationException {
    try {
      newFile.setData(cipher.doFinal(oldData));
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      throw new FatalImplementationException("Transformation '" + getTransformation() +"' is illegal", e);
    }
  }
  
  /**
   * Creates a {@link Key} from a {@link Key} which contains {@link SecretKey}s in order to work with {@link javax.crypto.Cipher}s.
   *
   * @return {@link Key} which can be used with {@link javax.crypto.Cipher}s.
   */
  public Map<String, Key> generateKey() throws ch.psc.domain.error.FatalImplementationException {
    KeyGenerator keyGenerator = new KeyGenerator();
    return keyGenerator.generateKey(getKeyBits(), getAlgorithm());
  }


}
