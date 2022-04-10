package ch.psc.domain.storage.service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.File;


/**
 * Beinhaltet Methoden um loklae File Operationen durchzuführen
 *
 * @author sevimrid, walchr01
 */
public class LocalStorage implements FileStorage {
  
  private String path;
  private int maxStorage;

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

  /**
   * Gibt einen double Wert zurueck mit dem freien Speicher in GB
   *
   * @return Groese des freien Speichers im System in GB
   */
  @Override
  public double getAvailableStorageSpace(){
    double size = 0;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    long startTime = System.currentTimeMillis();
    Future<Double> future = executorService.submit(() -> {
      double result = 0;
      java.io.File[] paths = java.io.File.listRoots();
      for(java.io.File path: paths){
        result += new java.io.File(path.toString()).getFreeSpace() / (1024.0 * 1024 * 1024);
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
  public Tree<File> getFileTree() {
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
