package ch.psc.domain.cipher;

import ch.psc.domain.error.FatalImplementationException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


/**
 * https://www.veracode.com/blog/research/encryption-and-decryption-java-cryptography
 * There are 2 key based encryption algorithms: Symmetric and Asymmetric algorithms.
 * There are various cryptographic parameters which need to be configured correctly for a crypto-system to be secured; these include key size, mode of operation, padding scheme, IV, etc.
 * For symmetric encryption use the AES algorithm. For asymmetric encryption, use the RSA algorithm.
 * Use a transformation that fully specifies the algorithm name, mode and padding. Most providers default to the highly insecure ECB mode of operation, if not specified.
 * Always use an authenticated mode of operation, i.e. AEAD (for example GCM or CCM) for symmetric encryption. If you have to use an unauthenticated mode, use CBC or CTR along with MAC to authenticate the ciphertext, correct random IV and padding parameters.
 * Use authentication tag with at least 128 bits length in AEAD modes.
 * Make sure to use OAEPWith<digest>And<mgf>Padding for asymmetric encryption, where the digest is SHA1/SHA256/384/512. Use PKCS5Padding for symmetric encryption.
 * If using PDKDF for key generation or Password Based Encryption (PBE), make sure to use SHA2 algorithms, a salt value of at least 64 bits and iteration count of 10,000.
 * Key sizes: use AES 256 if you can, else 128 is secure enough for time being. For RSA use at least 2048, consider 4096 or longer for future proofing.
 * There is a limit on how much plaintext can be safely encrypted using a single (key/IV) pair in CBC and CTR modes.
 * The randomness source of an IV comes from the IvParameterSpec class and not from init methods of the Cipher class.
 */

public class KeyGenerator {

    private static final int TO_INDEX = 1;

    public SecretKeySpec generateKey(int keyBits, String algorithm) throws FatalImplementationException{

        /**
        Generate an key using KeyGenerator + Initialize the provided keysize
         - Methode, welche prüft, ob algorithm und keyBites kompatibel sind?
         - To add to the complexity of a cipher, Initialization Vectors are used. Brauchen wir auch, vllt als Option?
        */
        SecretKeySpec secretKeySpec;
        try {
            javax.crypto.KeyGenerator keyGenerator = javax.crypto.KeyGenerator.getInstance(algorithm);
            keyGenerator.init(keyBits); // use 256 key bits if you can
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] raw = secretKey.getEncoded();
            secretKeySpec = new SecretKeySpec(raw, algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new FatalImplementationException("Transformation '" + algorithm + "' does not exist!", e);
        }
        saveCreatedKey(secretKeySpec);
        loadExistingKey();
        return secretKeySpec;
    }

    public IvParameterSpec generateInitVector(int keyBytes){
        SecureRandom srandom = new SecureRandom();
        byte[] iv = new byte[keyBytes];
        srandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        return ivParameterSpec;
    }

    //https://www.novixys.com/blog/java-aes-example/#3_Generate_an_Initialization_Vector_IV
    private void loadExistingKey() {
        File latestKeyFile = getLatestKeyFile();
    }

    private File getLatestKeyFile() {
        File keyFolder = new File("src/main/resources/KeyChain");
        try {
            List<File> filesInFolder = Files.walk(Paths.get(String.valueOf(keyFolder)))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            if (filesInFolder.size() != 0) {
                File latestKeyFile = filesInFolder.get(filesInFolder.size() - TO_INDEX);
                System.out.println(Files.readAllLines(Paths.get(String.valueOf(latestKeyFile)), Charset.defaultCharset()));
                return latestKeyFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveCreatedKey(SecretKeySpec skey) {
        File newArchiveFile = createNewKeyFile(skey);
        byte[] lineSeparator = System.getProperty("line.separator").getBytes(); // absatzbefehl in bytes
        try {
            FileOutputStream fos = new FileOutputStream(newArchiveFile, true);  // true for append mode
            fos.write(convertSecretKeyToString(skey).getBytes());
            fos.write("\n******************************************************".getBytes());
            fos.write(lineSeparator);
            fos.write("CREATED ON ".getBytes());
            String now = Instant.now().toString();
            fos.write(now.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createNewKeyFile(SecretKeySpec skey) {

        File keyFolder = new File("src/main/resources/KeyChain");
        String algo = skey.getAlgorithm();
        String dateToday = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault()).format(Instant.now());
        String createdOn = DateTimeFormatter.ofPattern("HHmmss").withZone(ZoneId.systemDefault()).format(Instant.now());
        File newKeyFile = new File(keyFolder.getPath() + "/keyFile_" + algo + "_" + dateToday + "_" + createdOn);
        boolean successfullyCreated;
        try {
            //logging hinzufügen
            successfullyCreated = newKeyFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newKeyFile;
    }

    private static String convertSecretKeyToString(SecretKeySpec secretKey) throws NoSuchAlgorithmException {
        byte[] rawData = secretKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }

}
