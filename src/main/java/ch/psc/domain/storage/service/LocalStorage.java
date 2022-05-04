package ch.psc.domain.storage.service;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.PscFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Contains methods to perform local file operations
 *
 * @author sevimrid, walchr01
 */
public class LocalStorage implements FileStorage {
  
  private String path;
  private int maxStorage;

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

  /**
   * Returns a double value with the free memory in GB
   *
   * @return Amount of free space in the system in GB
   */
  @Override
  public double getAvailableStorageSpace(){
    double size = 0;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    long startTime = System.currentTimeMillis();
    Future<Double> future = executorService.submit(() -> {
      double result = 0;
      File[] paths = File.listRoots();
      for(File path: paths){
        result += new File(path.toString()).getFreeSpace() / (1024.0 * 1024 * 1024);
      }
      return result;
    });
    while(!future.isDone()){
      double elapsedTimeInMillis = System.currentTimeMillis() - startTime;
    }
    try {
      size = future.get();
    }catch (InterruptedException | ExecutionException  e) {
      e.printStackTrace();
    }
    executorService.shutdown();
    return size;
  }
  
  @Override
  public Tree<PscFile> getFileTree() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getMaxStorage() {
    return maxStorage;
  }

  public void setMaxStorage(int maxStorage) {
    this.maxStorage = maxStorage;
  }
  
}
