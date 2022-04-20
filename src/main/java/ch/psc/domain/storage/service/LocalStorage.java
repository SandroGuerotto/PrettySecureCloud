package ch.psc.domain.storage.service;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.PscFile;

import java.util.List;
import java.io.File;
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
  private Tree<PscFile> tree;


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

  /**
   * Returns a file tree with all the files below the root
   *
   * @return File tree with path as root directory
   */
  @Override
  public Tree<PscFile> getFileTree() {
    Tree<PscFile> fileTree;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(()->{
      tree.getRoot().setValue(new PscFile());
      tree.getRoot().getValue().setPath(path);
      getSubtree(tree.getRoot());
    });
    executorService.shutdown();
    return tree;
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


  /**
   * Recursively creates the subtrees and saves them in the tree variable
   */
  private void getSubtree(Tree<PscFile>.Node<PscFile> parentNode){
    File parentContentFile = new File(parentNode.getValue().getPath());
    File[] parentContent = parentContentFile.listFiles();
    assert parentContent != null;
    if (parentContent.length!=0){
      for (File childFile : parentContent){
        PscFile folderCont = new PscFile();
        folderCont.setPath(childFile.getPath());
        if (childFile.isDirectory()){
          Tree<PscFile>.Node<PscFile> directoryNode = tree.new Node<PscFile>(parentNode,folderCont);
          parentNode.appendChild(directoryNode);
          getSubtree(directoryNode);
        }
        else {
          parentNode.appendChild(tree.new Node<PscFile>(parentNode,folderCont));
        }
      }
    }
  }
  
}
