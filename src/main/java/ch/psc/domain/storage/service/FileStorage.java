package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.ObjectProperty;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

public interface FileStorage {
  
  boolean upload(PscFile file, InputStream inputStream);

  InputStream download(PscFile file);

  BigDecimal getUsedStorageSpace();

  BigDecimal getTotalStorageSpace();
  
  List<PscFile> getFiles(String path);

  String getName();

  ObjectProperty<BigDecimal> getUsedStorageSpaceProperty();

  String getRoot();

}
