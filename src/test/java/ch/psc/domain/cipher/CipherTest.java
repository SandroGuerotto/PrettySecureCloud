package ch.psc.domain.cipher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.psc.domain.file.EncryptionState;
import ch.psc.domain.file.PscFile;
import ch.psc.exceptions.FatalImplementationException;

class CipherTest {

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

      @Override
      public int getNonceLength() {
        return 0;
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

      @Override
      public int getNonceLength() {
        return 0;
      }
    };

    file = new PscFile("/lorem/ipsum/dolor.file", "Lorem Ipsum");
    file.setData("Hello World!".getBytes());
    file.setEncryptionState(EncryptionState.DECRYPTED);
  }

  @Test
  public void generateKeyTestAES128() throws FatalImplementationException {
    Map<String, Key> generated = aesCipher.generateKey();

    assertEquals("AES", generated.get("AES").getType());
    assertEquals(128 / 8, generated.get("AES").getKey().getEncoded().length);
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
