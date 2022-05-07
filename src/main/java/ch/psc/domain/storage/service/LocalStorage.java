package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.DoubleProperty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;


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
    List<Future<PscFile>> uploadedFiles = new ArrayList<>();

    for (PscFile pscFile : files){
      try {
        File myObj = new File(pscFile.getPath());
        if (myObj.createNewFile()) {
          System.out.println("File created: " + myObj.getName());
        } else {
          System.out.println("File already exists.");
        }
        try (FileOutputStream fos = new FileOutputStream(pscFile.getPath())) {
          fos.write(pscFile.getData());
          uploadedFiles.add((Future<PscFile>) pscFile);
        }
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
    }
    return uploadedFiles;
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

  /**
   * Method returns PscFile list with all files and directories in a given path.
   *
   * @return List with all files in path
   */

  @Override
  public List<PscFile> getFiles(String path) {
    List<PscFile> fileList = new LinkedList<>();
    File directory = new File(path);
    File[] directoryContent = directory.listFiles();
    if (directoryContent!=null){
      for (File childFile : directoryContent){
        PscFile child = new PscFile(childFile.getPath(), childFile.getName(), childFile.isDirectory());
        System.out.println(childFile.getPath()+" "+childFile.getName()+ " "+childFile.isDirectory());
        fileList.add(child);
      }
    } else {
      return null;
    }
    return fileList;
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
