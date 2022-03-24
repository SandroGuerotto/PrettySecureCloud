package ch.psc.domain.storage.service;

public abstract class CloudService implements FileStorage {

  private String name;
  
  public CloudService(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
}
