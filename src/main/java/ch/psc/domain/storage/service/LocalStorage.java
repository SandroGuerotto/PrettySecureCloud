package ch.psc.domain.storage.service;

import java.util.List;
import java.util.concurrent.Future;
import ch.psc.domain.file.File;
import ch.psc.persistence.datastructure.Tree;

public class LocalStorage implements FileStorage {
  
  private String path;
  private int maxStorage;

  @Override
  public List<Future<File>> upload(List<File> files) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Future<File>> download(List<File> files) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getAvailableStorageSpace() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public Tree<File> getFileTree() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getMaxStorage() {
    return maxStorage;
  }

  public void setMaxStorage(int maxStorage) {
    this.maxStorage = maxStorage;
  }
  
}
