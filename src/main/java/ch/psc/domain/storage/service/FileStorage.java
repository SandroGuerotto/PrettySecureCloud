package ch.psc.domain.storage.service;

import java.util.List;
import java.util.concurrent.Future;
import ch.psc.domain.file.File;
import ch.psc.persistence.datastructure.Tree;

public interface FileStorage {
  
  public List<Future<File>> upload(List<File> files);
  
  public List<Future<File>> download(List<File> files);
  
  public int getAvailableStorageSpace();
  
  public Tree<File> getFileTree();
  
}
