package ch.psc.domain.cipher;

import ch.psc.domain.error.FatalImplementationException;
import ch.psc.domain.file.PscFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class RsaCipherTest {
  
  private static final String data = "Hello World!";

  private PscCipher cipher;
  private Key publicKey;
  private Key privateKey;

  private PscFile file1;
  
  @BeforeEach
  private void beforeEach() throws NoSuchAlgorithmException, FatalImplementationException {
    cipher = new RsaCipher();

    ch.psc.domain.cipher.KeyGenerator keyGenerator = new KeyGenerator();
    Map<String, Key> keyChain = keyGenerator.generateKey(cipher.getKeyBits(), cipher.getAlgorithm());

    for(String s : keyChain.keySet()){

      if (s.contains(".pub")){
        this.publicKey = keyChain.get(s);
      }else{
        this.privateKey = keyChain.get(s);
      }
    }
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
    List<Future<PscFile>> encList = cipher.encrypt(publicKey, Arrays.asList(file1));
    PscFile encrypted = encList.get(0).get();

    assertNotNull(file1.getNonce()); //expected side effect: set nonce
    assertArrayEquals(file1.getNonce(), encrypted.getNonce()); //decryption is impossible, if nonce does not match
  }
  
  @Test
  public void encryptDecryptTest() throws InterruptedException, ExecutionException {
    List<Future<PscFile>> encList = cipher.encrypt(publicKey, Arrays.asList(file1));
    PscFile encrypted = encList.get(0).get();
    
    List<Future<PscFile>> decList = cipher.decrypt(privateKey, Arrays.asList(encrypted));
    PscFile decrypted = decList.get(0).get();
    
    assertArrayEquals(file1.getData(), decrypted.getData());
  }

}
