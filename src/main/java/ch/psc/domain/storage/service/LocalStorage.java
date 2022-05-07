package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.ObjectProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
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
  public boolean upload(PscFile file, InputStream inputStream) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * This method will download a given file
   *
   * @param file to download
   * @return InputStream of the file to be downloaded
   */
  @Override
  public InputStream download(PscFile file) {
    FileInputStream fileInputStream = null;
    try{
      FileInputStream fin = new FileInputStream(file.getPath());
      fileInputStream = fin;
      fin.close();
    }catch(Exception e){
      e.printStackTrace();
    }
    return fileInputStream;
  }

  /**
   * Returns a double value with the free memory in GB
   *
   * @return Amount of free space in the system in GB
   */
  @Override
  public BigDecimal getUsedStorageSpace(){
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
    return new BigDecimal(size);
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
    return null;
  }

  @Override
  public ObjectProperty<BigDecimal> getUsedStorageSpaceProperty() {
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
