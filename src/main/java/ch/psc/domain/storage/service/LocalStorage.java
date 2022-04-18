package ch.psc.domain.storage.service;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.PscFile;

import java.util.List;
import java.util.concurrent.Future;

public class LocalStorage implements FileStorage {
  
  private String path;
  private int maxStorage;

  @Override
  public List<Future<PscFile>> upload(List<PscFile> files) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Future<PscFile>> download(List<PscFile> files) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double getAvailableStorageSpace() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public Tree<PscFile> getFileTree() {
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
