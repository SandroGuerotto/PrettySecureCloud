package ch.psc.domain.cipher;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.psc.domain.file.File;

class PlainTextCipherTest {
  
  private PlainTextCipher cipher;
  private Key key;
  private File file1;
  private File file2;
  private File file3;
  
  @BeforeEach
  private void beforeEach() {
    cipher = new PlainTextCipher();
    key = new Key();
    key.setKey("key".getBytes());
    key.setType(cipher.getType());
    file1 = new File();
    file1.setData("Hello World!".getBytes());
    file1.setEncryptionState(EncryptionState.DECRYPTED);
    file1.setName("file1");
    file1.setPath("test/file1");
    file2 = new File();
    file2.setData("üצה!£$ü?+\"*ח%&/()=<>^~".getBytes());
    file2.setEncryptionState(EncryptionState.DECRYPTED);
    file2.setName("file2");
    file2.setPath("dvelop.ch/file2");
    file3 = new File();
    file3.setData("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Purus gravida quis blandit turpis. Leo integer malesuada nunc vel risus commodo viverra maecenas. Neque egestas congue quisque egestas diam in arcu. Non blandit massa enim nec. Commodo odio aenean sed adipiscing. Tortor id aliquet lectus proin. Vulputate dignissim suspendisse in est ante. Viverra adipiscing at in tellus. Quis eleifend quam adipiscing vitae. Diam sollicitudin tempor id eu nisl nunc mi ipsum faucibus. Diam maecenas ultricies mi eget mauris pharetra et ultrices.".getBytes());
    file3.setEncryptionState(EncryptionState.DECRYPTED);
    file3.setName("file3");
    file3.setPath("/./path/to/the/file/file3");
  }
  
  @Test
  public void encryptionTest() throws InterruptedException, ExecutionException {
    List<File> fileList = Arrays.asList(file1);
    List<Future<File>> futureFiles = cipher.encrypt(key, fileList);
    File encrypted = futureFiles.get(0).get();
    
    assertEquals(EncryptionState.ENCRYPTED, encrypted.getEncryptionState());
    compareFileContents(file1, encrypted);
  }
  
  @Test
  public void decriptionTest() throws InterruptedException, ExecutionException {
    file1.setEncryptionState(EncryptionState.ENCRYPTED);
    List<File> fileList = Arrays.asList(file1);
    List<Future<File>> futureFiles = cipher.decrypt(key, fileList);
    File decrypted = futureFiles.get(0).get();
    
    assertEquals(EncryptionState.DECRYPTED, decrypted.getEncryptionState());
    compareFileContents(file1, decrypted);
  }
  
  @Test
  public void encryptMultipleTest() throws InterruptedException, ExecutionException {
    List<File> fileList = Arrays.asList(file1, file2, file3);
    List<Future<File>> futureFiles = cipher.encrypt(key, fileList);
    
    for(Future<File> future : futureFiles) {
      File file = future.get();
      assertEquals(EncryptionState.ENCRYPTED, file.getEncryptionState());
      compareFileContents(file);
    }
  }
  
  @Test
  public void decryptMultipleTest() throws InterruptedException, ExecutionException {
    List<File> fileList = Arrays.asList(file1, file2, file3);
    List<Future<File>> futureFiles = cipher.decrypt(key, fileList);
    
    for(Future<File> future : futureFiles) {
      File file = future.get();
      assertEquals(EncryptionState.DECRYPTED, file.getEncryptionState());
      compareFileContents(file);
    }
  }
  
  private void compareFileContents(File toCompare) {
    if(file1.getName().equals(toCompare.getName())) {
      compareFileContents(file1, toCompare);
    }
    else if(file2.getName().equals(toCompare.getName())) {
      compareFileContents(file2, toCompare);
    }
    else if(file3.getName().equals(toCompare.getName())) {
      compareFileContents(file3, toCompare);
    }
    else {
      fail("file name was messed up: " + toCompare.getName());
    }
  }
  
  private void compareFileContents(File expected, File actual) {
    assertArrayEquals(expected.getData(), actual.getData());
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getPath(), actual.getPath());
  }

}
