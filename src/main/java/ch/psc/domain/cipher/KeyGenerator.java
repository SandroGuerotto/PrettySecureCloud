package ch.psc.domain.cipher;

import ch.psc.exceptions.FatalImplementationException;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


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
 *
 *         Generate an key using KeyGenerator + Initialize the provided keysize
 *          - Methode, welche prüft, ob algorithm und keyBites kompatibel sind?
 *          - allenfalls auch eine abstrakte klasse mit entsprechenden implementationen? analog zu cipher
 *          - To add to the complexity of a cipher, Initialization Vectors are used. Brauchen wir auch, vllt als Option?
 *          - to consider: https://alex-labs.com/reasonably-secure-way-store-secret-java/
 */

public class KeyGenerator {


    public Map<String, Key> generateKey(int keyBits, String algorithm) throws FatalImplementationException {

        Map<String, Key> keyChain = new HashMap();

        try {
            javax.crypto.KeyGenerator keyGenerator = javax.crypto.KeyGenerator.getInstance(algorithm);
            keyGenerator.init(keyBits); // 128, 192 or 256 for AES
            SecretKey secretKey = keyGenerator.generateKey();
            //TODO currently only for 1 key, for 2 keys a loop must be considered here for each key to be added to the map
            Key key = new Key(secretKey);
            keyChain.put(algorithm, key);
        } catch (NoSuchAlgorithmException e) {
            throw new FatalImplementationException("Transformation '" + algorithm + "' does not exist!", e);
        }

        return keyChain;
    }

}
