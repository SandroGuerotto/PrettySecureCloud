package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.DoubleProperty;

import java.io.InputStream;
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
  public double getAvailableStorageSpace() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getTotalStorageSpace() {
    return 0;
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
  public DoubleProperty getUsedStorageSpaceProperty() {
    return null;
  }

    @Override
    public String getRoot() {
        return null;
    }

}
