package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.ObjectProperty;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

public class GoogleDriveService implements FileStorage {

  private final String name;

  public GoogleDriveService() {
    this.name = "Google Drive";
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean upload(PscFile file, InputStream inputStream) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public InputStream download(PscFile file) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BigDecimal getUsedStorageSpace() {
    // TODO Auto-generated method stub
    return new BigDecimal(0);
  }

  @Override
  public BigDecimal getTotalStorageSpace() {
    return new BigDecimal(0);
  }

  @Override
  public List<PscFile> getFiles(String path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ObjectProperty<BigDecimal> getUsedStorageSpaceProperty() {
    return null;
  }

    @Override
    public String getRoot() {
        return null;
    }

}
