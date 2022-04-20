package ch.psc.domain.cipher;

public class Key {
  
  private byte[] key;
  private String type;
  
  public byte[] getKey() {
    return key;
  }
  
  public void setKey(byte[] key) {
    this.key = key;
  }
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
}
