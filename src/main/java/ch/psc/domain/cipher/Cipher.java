package ch.psc.domain.cipher;

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

public abstract class Cipher {
  
  private ExecutorService executor = Executors.newFixedThreadPool(5);
  
  public abstract String getType();
  
  public abstract SecurityLevel getSecurityLevel();
  
  public abstract String getAlgorythm();
  
  public abstract String getTransformation();
  
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
  

  protected File encrypt(File file, SecretKey key) throws InvalidKeyException, FatalImplementationException {
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
  
  
  protected File decrypt(File file, SecretKey key) throws InvalidKeyException, FatalImplementationException {
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
  
  protected SecretKey generateKey(Key key) {
    SecretKey secretKey = new SecretKeySpec(key.getKey(), getAlgorythm());
    return secretKey;
  }
}
