package ch.psc.domain.cipher;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.psc.domain.file.PscFile;
import ch.psc.exceptions.FatalImplementationException;

class CipherTest {
  
  private static final String AES_128_KEY = "7CC52B54762BFD9726DF44F0422AB34D";
  private static final String AES_192_KEY = "4D92199549E0F2EF009B4160F3582E5528A11A45017F3EF8";

  private PscCipher unimplementedCipher;
  private PscCipher aesCipher;
  private PscFile file;
  
  @BeforeEach
  private void beforeEach() {
    unimplementedCipher = new PscCipher() {
      @Override
      public String getTransformation() {
        return null;
      }
      @Override
      public SecurityLevel getSecurityLevel() {
        return null;
      }
      @Override
      public String getAlgorythm() {
        return null;
      }
    };
    
    aesCipher = new PscCipher() {
      @Override
      public SecurityLevel getSecurityLevel() {
        return null;
      }
      @Override
      public String getAlgorythm() {
        return "AES";
      }
      @Override
      public String getTransformation() {
        return "AES";
      }
    };
    
    file = new PscFile();
    file.setData("Hello World!".getBytes());
    file.setEncryptionState(EncryptionState.DECRYPTED);
    file.setName("Lorem Ipsum");
    file.setPath("/lorem/ipsum/dolor.file");
    
  }
  
  @Test
  public void generateKeyTestAES128() {
    Key key = new Key();
    key.setKey(AES_128_KEY.getBytes());
    SecretKey generated = aesCipher.generateKey(key);
    
    assertEquals("AES", generated.getAlgorithm());
    assertArrayEquals(AES_128_KEY.getBytes(), generated.getEncoded());
  }
  
  @Test
  public void generateKeyTestAES192() {
    Key key = new Key();
    key.setKey(AES_192_KEY.getBytes());
    SecretKey generated = aesCipher.generateKey(key);
    
    assertEquals("AES", generated.getAlgorithm());
    assertArrayEquals(AES_192_KEY.getBytes(), generated.getEncoded());
  }
  
  @Test
  public void generateNullKeyTest() {
    Key key = new Key();
    key.setKey(null);
    
    assertThrows(IllegalArgumentException.class, () -> aesCipher.generateKey(key));
  }
  
  @Test
  public void generateUnimplementedKeyTest() {
    Key key = new Key();
    key.setKey(AES_192_KEY.getBytes());
    
    assertThrows(IllegalArgumentException.class, () -> unimplementedCipher.generateKey(key));
  }
  
  @Test
  public void getCipherTestAES() throws FatalImplementationException {
    javax.crypto.Cipher cipher = aesCipher.getCipher();
    
    assertEquals("AES", cipher.getAlgorithm());
  }
  
  @Test
  public void getCipherTestUnimplemented() {
    assertThrows(FatalImplementationException.class, () -> unimplementedCipher.getCipher());
  }

}
