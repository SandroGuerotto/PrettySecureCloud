package ch.psc.domain.cipher;

import ch.psc.domain.file.PscFile;

import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class RsaCipher extends PscCipher {
  
  private static final SecurityLevel SECURITY_LEVEL = SecurityLevel.high;
  private static final String ALGORITHM = "RSA";
  // https://www.devglan.com/java8/rsa-encryption-decryption-java
  // RSA/ECB/PKCS1Padding has been known to be insecure and you should use RSA/None/OAEPWithSHA1AndMGF1Padding instead.
  private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
  private static final int RSA_KEY_LENGTH = 2048;
  private static final int GCM_NONCE_LENGTH = 12;
  private static final int GCM_TAG_LENGTH = 128;


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

  /*
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
  */
}
