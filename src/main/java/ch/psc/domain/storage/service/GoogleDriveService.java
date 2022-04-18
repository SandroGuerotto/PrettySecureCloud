package ch.psc.domain.storage.service;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.PscFile;

import java.util.List;
import java.util.concurrent.Future;

public class GoogleDriveService extends CloudService {

  public GoogleDriveService() {
    super("Google Drive");
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
  public Tree<PscFile> getFileTree() {
    // TODO Auto-generated method stub
    return null;
  }

}
