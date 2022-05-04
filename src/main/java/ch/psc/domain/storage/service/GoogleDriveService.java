package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.DoubleProperty;

import java.util.List;
import java.util.concurrent.Future;

public class GoogleDriveService implements FileStorage {

  private final String name;

  public GoogleDriveService() {
    this.name = "Google Drive";
    // TODO Auto-generated constructor stub
  }

  @Override
  public List<Future<PscFile>> upload(List<PscFile> files) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Future<PscFile>> download(List<PscFile> files) {
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
