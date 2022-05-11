package ch.psc.domain.storage.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import ch.psc.domain.file.PscFile;
import javafx.beans.property.ObjectProperty;

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
