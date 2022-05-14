package ch.psc.domain.cipher;

import ch.psc.domain.file.EncryptionState;
import ch.psc.domain.file.PscFile;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Implementation of the abstract base class {@link PscCipher}.<br />
 * This class uses the AES algorithm to encrypt/decrypt {@link PscFile}s.
 *
 * @author Tristan, Lorenz
 */
public class AesCipher extends PscCipher {

    private static final SecurityLevel SECURITY_LEVEL = SecurityLevel.high;
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int AES_KEY_LENGTH = 256;
    private static final int GCM_NONCE_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    @Override
    public SecurityLevel getSecurityLevel() {
        return SECURITY_LEVEL;
    }

    @Override
    public int getKeyBits() {
        return AES_KEY_LENGTH;
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
      return GCM_NONCE_LENGTH;
    }
    
    /**
     * This method is a specification for the AES algorithm which makes use of
     * so called initialization vectors (IV). The relevant parameters (nonce) must be
     * randomnized each time the is being encrypted.
     *
     * @param file {@link PscFile}
     * @return AlgorithmParameterSpec - a new one if the file is currently decrypted,
     * and the already existing one, if the file already encrypted (the used nonce is
     * stored in the {@link PscFile} and can be obtained via {@link PscFile#getNonce()}
     */
    @Override
    public AlgorithmParameterSpec getAlgorithmSpecification(PscFile file) {
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        if (EncryptionState.DECRYPTED == file.getEncryptionState()) {
            SecureRandom random = new SecureRandom();
            random.nextBytes(nonce);
            file.setNonce(nonce);
        } else {
            nonce = file.getNonce();
        }
        return new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
    }

}
