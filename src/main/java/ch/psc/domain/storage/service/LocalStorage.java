package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.DoubleProperty;

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
  private String name;
  private double maxStorage;


  public LocalStorage(){
    setMaxStorage();
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
  public double getTotalStorageSpace() {
    return 100;
  }

  @Override
  public List<PscFile> getFiles(String path) {


    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public DoubleProperty getUsedStorageSpaceProperty() {
    return null;
  }

    @Override
    public String getRoot() {
        return null;
    }

    public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public double getMaxStorage() {
    return maxStorage;
  }

  private void setMaxStorage() {
    File[] paths = File.listRoots();
    for(File path: paths){
      this.maxStorage += new File(path.toString()).getTotalSpace() / (1024.0 * 1024 * 1024);
    }
  }
  
}
