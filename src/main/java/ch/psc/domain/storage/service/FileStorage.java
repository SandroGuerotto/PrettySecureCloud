package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.DoubleProperty;

import java.io.InputStream;
import java.util.List;

public interface FileStorage {
  
  boolean upload(PscFile file, InputStream inputStream);

  InputStream download(PscFile file);
  
  double getAvailableStorageSpace();

  double getTotalStorageSpace();
  
  List<PscFile> getFiles(String path);

  String getName();

  DoubleProperty getUsedStorageSpaceProperty();

  String getRoot();

}
