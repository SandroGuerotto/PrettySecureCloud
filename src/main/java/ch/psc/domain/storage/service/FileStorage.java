package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.concurrent.Future;

public interface FileStorage {
  
  List<Future<PscFile>> upload(List<PscFile> files);
  
  List<Future<PscFile>> download(List<PscFile> files);
  
  double getAvailableStorageSpace();

  double getTotalStorageSpace();
  
  List<PscFile> getFiles(String path);

  String getName();

  DoubleProperty getUsedStorageSpaceProperty();

  String getRoot();

}
