package ch.psc.domain.storage.service;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.File;

import java.util.List;
import java.util.concurrent.Future;

public interface FileStorage {
  
  List<Future<File>> upload(List<File> files);
  
  List<Future<File>> download(List<File> files);
  
  int getAvailableStorageSpace();
  
  Tree<File> getFileTree();

  void login();
  
}
