package ch.psc.domain.cipher;

public class RsaCipher extends PscCipher {
  
  private static final SecurityLevel SECURITY_LEVEL = SecurityLevel.high;
  private static final String ALGORITHM = "RSA";
  // https://www.devglan.com/java8/rsa-encryption-decryption-java
  // RSA/ECB/PKCS1Padding has been known to be insecure and you should use RSA/None/OAEPWithSHA1AndMGF1Padding instead.
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

}
