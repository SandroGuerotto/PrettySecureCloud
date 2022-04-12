import ch.psc.domain.cipher.KeyGenerator;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class EncryptionDecryption {

    @Test
    public void whenIsEncryptedAndDecrypted_thenDecryptedEqualsOriginal()
            throws Exception {

        String originalMessage = "This is a secret message";
        int encryptionKeyBits = 128;

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        KeyGenerator keyGenerator = new KeyGenerator();
        SecretKey secretKey = keyGenerator.generateKey(encryptionKeyBits, "AES");

        // IvParameterSpec ivParameterSpec = keyGenerator.generateInitVector(encryptionKeyBits/8);
        // cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedMessageBytes = cipher.doFinal(originalMessage.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessageBytes);
        assert(originalMessage).equals(new String(decryptedMessageBytes));
    }

}
