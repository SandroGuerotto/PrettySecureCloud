package ch.psc.domain.cipher;

import javax.crypto.SecretKey;

public class Key {

  private final SecretKey key;

  public Key(SecretKey secretKey){
    this.key = secretKey;
  }

  public SecretKey getKey() {
    return key;
  }

  public String getType() {
    return key.getAlgorithm();
  }
}
