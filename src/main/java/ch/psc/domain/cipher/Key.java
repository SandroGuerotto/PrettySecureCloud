package ch.psc.domain.cipher;

import javax.crypto.SecretKey;

public class Key {

  private SecretKey key;
  private String type;
  
  public SecretKey getKey() {
    return key;
  }

  public void setKey(SecretKey key) {
    this.key = key;
  }
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
}
