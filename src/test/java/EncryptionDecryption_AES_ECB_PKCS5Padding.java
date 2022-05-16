import java.util.Map;
import javax.crypto.Cipher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.cipher.KeyGenerator;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EncryptionDecryption_AES_ECB_PKCS5Padding {

  public static final String ALGORITHM = "AES";
  private static KeyGenerator keyGenerator;
  private static Map<String, Key> keyChain;
  private static Cipher cipher;
  private static final int ENCRYPTION_KEY_BITS = 128;


  @BeforeAll
  public static void setup() throws Exception {
    cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    keyGenerator = new KeyGenerator();
    keyChain = keyGenerator.generateKey(ENCRYPTION_KEY_BITS, ALGORITHM);
  }

  @Test
  public void encryptAndDecryptMessageSubsequentlyCompare() throws Exception {

    cipher.init(Cipher.ENCRYPT_MODE, keyChain.get(ALGORITHM).getKey());

    String originalMessage = "This is a secret message";

    byte[] encryptedMessageBytes = cipher.doFinal(originalMessage.getBytes());

    cipher.init(Cipher.DECRYPT_MODE, keyChain.get(ALGORITHM).getKey());

    byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessageBytes);

    assert (originalMessage).equals(new String(decryptedMessageBytes));
  }



}
