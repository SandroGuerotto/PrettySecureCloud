package ch.psc.domain.storage.service;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.PscFile;

import java.util.List;
import java.util.concurrent.Future;

public interface FileStorage {
  
  List<Future<PscFile>> upload(List<PscFile> files);
  
  List<Future<PscFile>> download(List<PscFile> files);
  
  double getAvailableStorageSpace();
  
  Tree<PscFile> getFileTree();
}
