package ch.psc.domain.file;

import ch.psc.domain.cipher.EncryptionState;

public class File {
  
  private String name;

  private String path;
  
  private byte[] data;
  
  private EncryptionState encryptionState;
  
  public int getFileSize() {
    //TODO
    return -1;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }
  
  public EncryptionState getEncryptionState() {
    return encryptionState;
  }
  
  public void setEncryptionState(EncryptionState encryptionState) {
    this.encryptionState = encryptionState;
  }
}
