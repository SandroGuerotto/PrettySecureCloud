package ch.psc.domain.cipher;

public class Key {

  private final java.security.Key key;

  public Key(java.security.Key secretKey){
    this.key = secretKey;
  }

  public java.security.Key getKey() {
    return key;
  }

  public String getType() {
    return key.getAlgorithm();
  }

}
