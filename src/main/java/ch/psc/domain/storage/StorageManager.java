package ch.psc.domain.storage;

import java.util.List;
import java.util.concurrent.Future;
import ch.psc.domain.file.File;
import ch.psc.domain.storage.service.FileStorage;
import ch.psc.persistence.datastructure.Tree;

public class StorageManager {
  
  private Tree<File> managedFiles;
  private List<FileStorage> storageOptions;

  public void uploadFiles(List<File> files) {
    //TODO
  }
  
  public List<Future<File>> downloadFiles(List<File> files) {
    //TODO
    return null;
  }
  
  public Tree<File> getManagedFiles() {
    return managedFiles;
  }
  
  public List<FileStorage> getStorageOptions() {
    return storageOptions;
  }
}
