package ch.psc.domain.file;

import ch.psc.domain.cipher.EncryptionState;

public class PscFile {
  
  private final String name;
  private final String path;
  private byte[] data;
  private final boolean isDirectory;
  private EncryptionState encryptionState;
  public PscFile(String name, String path) {
    this(path,name,false);
  }
  public PscFile(String name, String path, boolean isDirectory) {
    this.path = path;
    this.name = name;
    this.isDirectory = isDirectory;
  }
  public PscFile(){
    this.path = "";
    this.name = "";
    this.isDirectory = false;
  }

  public int getFileSize() {
    //TODO
    return -1;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public byte[] getData() {
    return data;
  }
  
  public EncryptionState getEncryptionState() {
    return encryptionState;
  }
  
  public void setEncryptionState(EncryptionState encryptionState) {
    this.encryptionState = encryptionState;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public boolean isDirectory() {
    return isDirectory;
  }
}
