package ch.psc.domain.cipher;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import ch.psc.domain.file.PscFile;

public class AesCipher extends PscCipher {
  
  private static final SecurityLevel SECURITY_LEVEL = SecurityLevel.high;
  private static final String ALGORYTHM = "AES";
  private static final String TRANSFORMATION = "AES/GCM/NoPadding";
  private static final int AES_KEY_LENGTH = 256;
  private static final int GCM_NONCE_LENGTH = 12;
  private static final int GCM_TAG_LENGTH = 128;
  
  @Override
  public SecurityLevel getSecurityLevel() {
    return SECURITY_LEVEL;
  }

  @Override
  public String getAlgorythm() {
    return ALGORYTHM;
  }

  @Override
  public String getTransformation() {
    return TRANSFORMATION;
  }
  
  @Override
  public AlgorithmParameterSpec getAlgorithmSpecification(PscFile file) {
    byte[] nonce = new byte[GCM_NONCE_LENGTH];
    if(EncryptionState.DECRYPTED == file.getEncryptionState()) {
      SecureRandom random = new SecureRandom();
      random.nextBytes(nonce);
      file.setNonce(nonce);
    }
    else {
      nonce = file.getNonce();
    }
    return new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
  }

}
