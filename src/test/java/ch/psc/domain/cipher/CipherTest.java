package ch.psc.domain.cipher;

import ch.psc.domain.file.EncryptionState;
import ch.psc.domain.file.PscFile;
import ch.psc.exceptions.FatalImplementationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
      public int getKeyBits() {
        return 0;
      }

      @Override
      public String getAlgorithm() {
        return null;
      }
    };
    
    aesCipher = new PscCipher() {
      @Override
      public SecurityLevel getSecurityLevel() {
        return null;
      }
      @Override
      public String getAlgorithm() {
        return "AES";
      }
      @Override
      public String getTransformation() {
        return "AES";
      }
      @Override
      public int getKeyBits() {
        return 128;
      }
    };
    
    file = new PscFile("/lorem/ipsum/dolor.file","Lorem Ipsum");
    file.setData("Hello World!".getBytes());
    file.setEncryptionState(EncryptionState.DECRYPTED);
  }
  
  @Test
  public void generateKeyTestAES128() throws FatalImplementationException {
    Map<String, Key> generated = aesCipher.generateKey();

    assertEquals("AES", generated.get("AES").getType());
    assertEquals(128/8, generated.get("AES").getKey().getEncoded().length);
  }

  @Test
  public void generateUnimplementedKeyTest() {
    assertThrows(NullPointerException.class, () -> unimplementedCipher.generateKey());
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
