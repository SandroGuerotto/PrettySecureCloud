package ch.psc.domain.cipher;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.psc.domain.file.PscFile;

class AesCipherTest {
  
  private static final String data = "Hello World!";
  /**
   * exactly 128 bits
   */
  private static final String pass = "MySecretKey_1234";

  private PscCipher cipher;
  private Key key;
  private PscFile file1;
  
  @BeforeEach
  private void beforeEach() {
    cipher = new AesCipher();
    key = new Key(new SecretKeySpec(pass.getBytes(), cipher.getAlgorithm()));
    file1 = new PscFile();
    file1.setData(data.getBytes());
    file1.setName("file1");
    file1.setPath("foo/bar/baz/hello.txt");
    file1.setEncryptionState(EncryptionState.DECRYPTED);
  }
  
  @Test
  public void encryptTooShortKey() {
    Key invalidKey = new Key(new SecretKeySpec("tooShort".getBytes(), cipher.getAlgorithm()));

    assertThrows(ExecutionException.class, () -> {
      List<Future<PscFile>> encList = cipher.encrypt(invalidKey, Arrays.asList(file1));
      encList.get(0).get();
    });
  }
  
  @Test
  public void decryptTooShortKey() {
    Key invalidKey = new Key(new SecretKeySpec("tooShort".getBytes(), cipher.getAlgorithm()));
    
    assertThrows(ExecutionException.class, () -> {
      List<Future<PscFile>> decList = cipher.decrypt(invalidKey, Arrays.asList(file1));
      decList.get(0).get();
    });
  }
  
  @Test
  public void nonceTest() throws InterruptedException, ExecutionException {
    List<Future<PscFile>> encList = cipher.encrypt(key, Arrays.asList(file1));
    PscFile encrypted = encList.get(0).get();

    assertNotNull(file1.getNonce()); //expected side effect: set nonce
    assertArrayEquals(file1.getNonce(), encrypted.getNonce()); //decryption is impossible, if nonce does not match
  }
  
  @Test
  public void randomResultTest() throws InterruptedException, ExecutionException {
    List<Future<PscFile>> encList1 = cipher.encrypt(key, Arrays.asList(file1));
    List<Future<PscFile>> encList2 = cipher.encrypt(key, Arrays.asList(file1));
    
    //The nonce should have prevented the same outcome to prevent brute force attacks.
    assertNotEquals(encList1.get(0).get().getData(), encList2.get(0).get().getData());
  }
  
  @Test
  public void encryptDecryptTest() throws InterruptedException, ExecutionException {
    List<Future<PscFile>> encList = cipher.encrypt(key, Arrays.asList(file1));
    PscFile encrypted = encList.get(0).get();
    
    List<Future<PscFile>> decList = cipher.decrypt(key, Arrays.asList(encrypted));
    PscFile decrypted = decList.get(0).get();
    
    assertArrayEquals(file1.getData(), decrypted.getData());
  }
  
}
