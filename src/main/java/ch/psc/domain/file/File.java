package ch.psc.domain.file;

public class File {
  
  private String name;

  private String path;
  
  private byte[] data;
  
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
  
}
