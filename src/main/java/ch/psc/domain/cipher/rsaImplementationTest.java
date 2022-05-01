package ch.psc.domain.cipher;
import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class rsaImplementationTest {

    /**
     * @author Anass AIT BEN EL ARBI
     * <ul>
     *     <li>RSA/ECB/PKCS1Padding (1024, 2048)</li>
     * </ul>
     * <p>
     * for more details @see <a href="https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html">Java Ciphers</a>
     */

    public static class rsaRun {

        private static PrivateKey privateKey;
        private static PublicKey publicKey;


        public static String encrypt(String message) throws Exception{
            byte[] messageToBytes = message.getBytes();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,publicKey);
            byte[] encryptedBytes = cipher.doFinal(messageToBytes);
            return encode(encryptedBytes);
        }
        private static String encode(byte[] data){
            return Base64.getEncoder().encodeToString(data);
        }

        public static String decrypt(String encryptedMessage) throws Exception{
            byte[] encryptedBytes = decode(encryptedMessage);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE,privateKey);
            byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
            return new String(decryptedMessage,"UTF8");
        }
        private static byte[] decode(String data){
            return Base64.getDecoder().decode(data);
        }

        public static void main(String[] args) {
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(1024);
                KeyPair pair = generator.generateKeyPair();
                privateKey = pair.getPrivate();
                publicKey = pair.getPublic();
            } catch (Exception ignored) {
            }
            try{
                String encryptedMessage = rsaRun.encrypt("Hello World");
                String decryptedMessage = rsaRun.decrypt(encryptedMessage);

                System.err.println("Encrypted:\n"+encryptedMessage);
                System.err.println("Decrypted:\n"+decryptedMessage);
            }catch (Exception e){}
        }
    }
}
