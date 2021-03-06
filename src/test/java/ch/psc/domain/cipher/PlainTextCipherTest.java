package ch.psc.domain.cipher;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.psc.domain.file.EncryptionState;
import ch.psc.domain.file.PscFile;

class PlainTextCipherTest {

  private PlainTextCipher cipher;
  private Key key;
  private PscFile file1;
  private PscFile file2;
  private PscFile file3;

  @BeforeEach
  private void beforeEach() {
    cipher = new PlainTextCipher();
    key = new Key(new SecretKeySpec("FooBarBaz".getBytes(), "PlainText"));
    file1 = new PscFile("file1", "test/file1");
    file1.setData("Hello World!".getBytes());
    file1.setEncryptionState(EncryptionState.DECRYPTED);
    file2 = new PscFile("file2", "dvelop.ch/file2");
    file2.setData("���!�$�?+\"*�%&/()=<>^~".getBytes());
    file2.setEncryptionState(EncryptionState.DECRYPTED);
    file3 = new PscFile("file3", "/./path/to/the/file/file3");
    file3.setData(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Purus gravida quis blandit turpis. Leo integer malesuada nunc vel risus commodo viverra maecenas. Neque egestas congue quisque egestas diam in arcu. Non blandit massa enim nec. Commodo odio aenean sed adipiscing. Tortor id aliquet lectus proin. Vulputate dignissim suspendisse in est ante. Viverra adipiscing at in tellus. Quis eleifend quam adipiscing vitae. Diam sollicitudin tempor id eu nisl nunc mi ipsum faucibus. Diam maecenas ultricies mi eget mauris pharetra et ultrices."
            .getBytes());
    file3.setEncryptionState(EncryptionState.DECRYPTED);
  }

  @Test
  public void encryptionTest() throws InterruptedException, ExecutionException {
    List<PscFile> fileList = Arrays.asList(file1);
    List<Future<PscFile>> futureFiles = cipher.encrypt(key, fileList);
    PscFile encrypted = futureFiles.get(0).get();

    assertEquals(EncryptionState.ENCRYPTED, encrypted.getEncryptionState());

    PscFile exp = new PscFile("file1.psc", "test/file1");
    exp.setData("Hello World!".getBytes());

    compareFileContents(exp, encrypted);
  }

  @Test
  public void decryptionTest() throws InterruptedException, ExecutionException {
    file1.setEncryptionState(EncryptionState.ENCRYPTED);
    List<PscFile> fileList = Arrays.asList(file1);
    List<Future<PscFile>> futureFiles = cipher.decrypt(key, fileList);
    PscFile decrypted = futureFiles.get(0).get();

    assertEquals(EncryptionState.DECRYPTED, decrypted.getEncryptionState());

    PscFile exp = new PscFile("file1", "test/file1");
    exp.setData("Hello World!".getBytes());

    compareFileContents(exp, decrypted);
  }

  @Test
  public void encryptMultipleTest() throws InterruptedException, ExecutionException {
    List<PscFile> fileList = Arrays.asList(file1, file2, file3);
    List<Future<PscFile>> futureFiles = cipher.encrypt(key, fileList);

    for (Future<PscFile> future : futureFiles) {
      PscFile act = future.get();
      assertEquals(EncryptionState.ENCRYPTED, act.getEncryptionState());

      PscFile exp = new PscFile(act.getName(), act.getPath());
      exp.setData(act.getData());

      compareFileContents(exp, act);
    }
  }

  @Test
  public void decryptMultipleTest() throws InterruptedException, ExecutionException {
    List<PscFile> fileList = Arrays.asList(file1, file2, file3);
    List<Future<PscFile>> futureFiles = cipher.decrypt(key, fileList);

    for (Future<PscFile> future : futureFiles) {
      PscFile act = future.get();
      assertEquals(EncryptionState.DECRYPTED, act.getEncryptionState());

      PscFile exp = new PscFile(act.getName(), act.getPath());
      exp.setData(act.getData());

      compareFileContents(exp, act);
    }
  }

  private void compareFileContents(PscFile expected, PscFile actual) {
    assertArrayEquals(expected.getData(), actual.getData());
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getPath(), actual.getPath());
  }

}
