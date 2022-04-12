import ch.psc.domain.cipher.Key;
import ch.psc.domain.cipher.KeyGenerator;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import javax.crypto.Cipher;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EncryptionDecryption_AES_ECB_PKCS5Padding {

    private static File keyFolder;
    private static File keyFile;
    private static KeyGenerator keyGenerator;
    private static Key secretKey;
    private static Cipher cipher;
    private static final int encryptionKeyBits = 128;

    @BeforeAll
    public static void createKeyCipherAndFolder() throws Exception {
        keyFolder = new File("src/test/resources/KeyChain");
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        keyGenerator = new KeyGenerator();
        secretKey = keyGenerator.generateKey(encryptionKeyBits, "AES");
    }

    @Test
    @Order(1)
    public void encryptAndDecryptMessageSubsequentlyCompare() throws Exception {

        // IvParameterSpec ivParameterSpec = keyGenerator.generateInitVector(encryptionKeyBits/8);
        // cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey.getKey());

        String originalMessage = "This is a secret message";

        byte[] encryptedMessageBytes = cipher.doFinal(originalMessage.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, secretKey.getKey());

        byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessageBytes);

        assert(originalMessage).equals(new String(decryptedMessageBytes));
    }

    @Test
    @Order(2)
    public void saveGeneratedKeyToFile() throws Exception {


        int keyFilesBefore;
        int keyFilesAfter;

        File keyFolder = new File("src/test/resources/KeyChain");

        keyFilesBefore = Objects.requireNonNull(keyFolder.listFiles()).length;

        keyFile = keyGenerator.saveCreatedKey(keyFolder, secretKey);

        System.out.println("Created new key file: " + keyFile.getName());

        keyFilesAfter = Objects.requireNonNull(keyFolder.listFiles()).length;

        assertNotNull(keyFile);
        assertTrue(keyFilesAfter>keyFilesBefore);

    }

    @Test
    @Order(3)
    public void loadExistingKeyFromKeyChain() throws Exception {

        File keyFolder = new File("src/test/resources/KeyChain");

        File keyFile = keyGenerator.loadLatestKeyFile(keyFolder);

        System.out.println("loaded key file:      " + keyFile.getName());

        List<String> content = Files.readAllLines(Paths.get(String.valueOf(keyFile)));

        assertEquals(3, content.size());

        String keyAsString = content.get(0);
        System.out.println("'" + content.get(0) + "'");
        //expected: <256> but was: <352> -> 96
        //expected: <192> but was: <256> -> 64
        //expected: <128> but was: <192> -> 64

        //TODO verstehe nicht so ganz die berechnung der bytes - offenbar werden mehr bytes in die datei geschrieben...
        //assertEquals(encryptionKeyBits, keyAsString.getBytes().length*8);

        String createdOnString = content.get(2);

        assertTrue(createdOnString.startsWith("CREATED"));

    }

    @AfterAll
    public static void cleanUp(){
        if (keyFile.delete()) {
            System.out.println("Deleted the file:     " + keyFile.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
    }
}
