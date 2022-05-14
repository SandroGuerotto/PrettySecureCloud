package ch.psc.domain.cipher;

import java.util.Map;
import ch.psc.domain.file.PscFile;

/**
 * Implementation of the abstract base class {@link PscCipher}.<br />
 * This class uses the RSA algorithm to encrypt/decrypt {@link PscFile}s.
 *
 * @author Tristan, Lorenz
 */
public class RsaCipher extends PscCipher {

    private static final SecurityLevel SECURITY_LEVEL = SecurityLevel.high;
    private static final String ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final int RSA_KEY_LENGTH = 2048;


    @Override
    public SecurityLevel getSecurityLevel() {
        return SECURITY_LEVEL;
    }

    @Override
    public int getKeyBits() {
        return RSA_KEY_LENGTH;
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public String getTransformation() {
        return TRANSFORMATION;
    }
    
    @Override
    public int getNonceLength() {
        return 0;
    }

    /**
     * @return The Key with {@link RsaCipher#getAlgorithm()} + {@link KeyGenerator#PUBLIC_KEY_POSTFIX} as key-value.
     */
    @Override
    public Key findDecryptionKey(Map<String, Key> keyChain) {
        return keyChain.get(getAlgorithm() + KeyGenerator.PUBLIC_KEY_POSTFIX);
    }
}
