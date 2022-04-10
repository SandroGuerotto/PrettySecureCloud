package ch.psc.domain.storage.service;

import java.util.List;
import java.util.concurrent.Future;
import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.File;

public class GoogleDriveService extends CloudService {

  public GoogleDriveService() {
    super("Google Drive");
    // TODO Auto-generated constructor stub
  }

  @Override
  public List<Future<File>> upload(List<File> files) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Future<File>> download(List<File> files) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double getAvailableStorageSpace() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Tree<File> getFileTree() {
    // TODO Auto-generated method stub
    return null;
  }

}
