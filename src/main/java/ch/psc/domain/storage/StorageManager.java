package ch.psc.domain.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import ch.psc.domain.cipher.CipherFactory;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.cipher.KeyGenerator;
import ch.psc.domain.cipher.PlainTextCipher;
import ch.psc.domain.cipher.PscCipher;
import ch.psc.domain.common.context.AuthenticationContext;
import ch.psc.domain.file.EncryptionState;
import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.service.FileStorage;
import ch.psc.domain.storage.service.StorageServiceFactory;
import ch.psc.domain.user.User;
import ch.psc.exceptions.FatalImplementationException;

/**
 * Handles all communication with the cipher implementation and storage service.
 */
public class StorageManager {

  private List<FileStorage> storageOptions;
  private final User user;
  private final ExecutorService executorService;

  public StorageManager(User user) {
    this.user = user;
    executorService = Executors.newCachedThreadPool();
  }

  /**
   * Encrypts the file and uploads it to the selected storage service. After each encrypting process
   * {@link ProcessState} the callback function is called with the appropriate event.
   *
   * @param storage selected storage service
   * @param file file to upload
   * @param callback callback function after each process event
   */
  public void uploadFiles(FileStorage storage, File file, Consumer<ProcessState> callback) {
    executorService.submit(() -> {
      try {
        callback.accept(ProcessState.ENCRYPTING);
        PscFile encrypted = encrypt(file);
        InputStream inputStream = createUploadStream(encrypted);
        callback.accept(ProcessState.ENCRYPTED);

        callback.accept(ProcessState.UPLOADING);
        storage.upload(encrypted.getName(), inputStream);
        callback.accept(ProcessState.UPLOADED);

      } catch (IOException | FatalImplementationException | InterruptedException
          | ExecutionException e) {
        e.printStackTrace();
      }
      callback.accept(ProcessState.FINISHED);
    });
  }

  /**
   * Creates a {@link ByteArrayInputStream} from a file. Combines nonce and data bytes together.
   *
   * @param encrypted encrypted file
   * @return {@link ByteArrayInputStream} with nonce and data bytes
   */
  private InputStream createUploadStream(PscFile encrypted) {
    int nonceLenght = 0;
    if(encrypted.getNonce() != null) {
      nonceLenght += encrypted.getNonce().length;
    }
    byte[] encryptedBytes = new byte[nonceLenght + encrypted.getData().length];
    
    if(encrypted.getNonce() != null) {
      System.arraycopy(encrypted.getNonce(), 0, encryptedBytes, 0, encrypted.getNonce().length);
    }
    System.arraycopy(encrypted.getData(), 0, encryptedBytes, nonceLenght, encrypted.getData().length);
    return new ByteArrayInputStream(encryptedBytes);
  }

  /**
   * Downloads a selected file from the selected storage services and encrypts it if necessary.
   * After each decrypting process {@link ProcessState} the callback function is called with the
   * appropriate event.
   *
   * @param storage selected storage service
   * @param file file to download
   * @param callback callback function after each process event
   */
  public void downloadFiles(FileStorage storage, PscFile file, Consumer<ProcessState> callback) {
    executorService.submit(() -> {
      callback.accept(ProcessState.DOWNLOADING);
      InputStream inputStream = storage.download(file.getPath());
      try {
        callback.accept(ProcessState.DOWNLOADED);
        callback.accept(ProcessState.DECRYPTING);
        if (file.getEncryptionState().equals(EncryptionState.ENCRYPTED))
          inputStream = decrypt(file, inputStream);
        callback.accept(ProcessState.DECRYPTED);

        writeDecryptedFile(downloadPath(file.getName()), inputStream);


      } catch (IOException | FatalImplementationException | InterruptedException
          | ExecutionException e) {
        e.printStackTrace();
      }

      callback.accept(ProcessState.FINISHED);
    });
  }

  private String downloadPath(String fileName) {
    return user.getDownloadPath() + System.getProperty("file.separator")
        + fileName.replace(PscFile.PSC_FILE_EXTENSION, "");
  }

  /**
   * Loads the current directory from the selected storage service and return a list to the callback
   * function
   *
   * @param storage active storage
   * @param path current directory
   * @param callback callback function after directory is loaded
   */
  public void loadManagedFiles(FileStorage storage, String path, Consumer<List<PscFile>> callback) {
    if (storage != null) {
      executorService.submit(() -> callback.accept(storage.getFiles(path)));
    }
  }

  /**
   * Returns all connected storage services by the user.
   *
   * @return registered storage services
   */
  public List<FileStorage> getStorageOptions() {
    return storageOptions;
  }

  /**
   * Creates all connected storage services.
   */
  public void loadStorageServices() {
    storageOptions = user.getStorageServiceConfig().entrySet().stream()
        .map(entry -> StorageServiceFactory.createService(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
    try {
      AuthenticationContext.getAuthService().update(user);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Takes a file, reads all bytes and encrypts it with the user-selected cipher-type.
   *
   * @param unencrypted unencrypted file
   * @return encrypted file
   * @throws IOException if source con not be read
   * @throws FatalImplementationException if cipher-type is invalid
   * @throws InterruptedException if encrypting failed
   * @throws ExecutionException if encrypting failed
   */
  private PscFile encrypt(File unencrypted)
      throws IOException, FatalImplementationException, InterruptedException, ExecutionException {
    PscFile pscFile = new PscFile(unencrypted.getName(), unencrypted.getPath(),
        unencrypted.length(), null, false);
    try (FileInputStream is = new FileInputStream(unencrypted)) {
      pscFile.setData(is.readAllBytes());
    }
    PscCipher cipher = findFirstCipher();
    List<Future<PscFile>> futureFiles =
        cipher.encrypt(cipher.findEncryptionKey(user.getKeyChain()), Arrays.asList(pscFile));
    return futureFiles.get(0).get();
  }

  /**
   * @param file target file
   * @param inputStream source input
   * @return {@link ByteArrayInputStream} with decrypted data
   * @throws IOException if source con not be read
   * @throws FatalImplementationException if cipher-type is invalid
   * @throws InterruptedException if decrypting failed
   * @throws ExecutionException if decrypting failed
   */
  private InputStream decrypt(PscFile file, InputStream inputStream)
      throws FatalImplementationException, IOException, InterruptedException, ExecutionException {
    PscCipher cipher = findFirstCipher();
    byte[] fileContent = inputStream.readAllBytes();

    assert (fileContent != null);
    assert (fileContent.length > 0);
    byte[] nonce = new byte[cipher.getNonceLength()];
    byte[] data = new byte[fileContent.length - cipher.getNonceLength()];
    System.arraycopy(fileContent, 0, nonce, 0, cipher.getNonceLength());
    System.arraycopy(fileContent, cipher.getNonceLength(), data, 0,
        fileContent.length - cipher.getNonceLength());

    file.setData(data);
    file.setNonce(nonce);

    List<Future<PscFile>> decFiles =
        cipher.decrypt(cipher.findDecryptionKey(user.getKeyChain()), List.of(file));
    return new ByteArrayInputStream(decFiles.get(0).get().getData());
  }

  /**
   * Writes data to given target path
   *
   * @param path target path
   * @param inputStream data source
   * @throws IOException if write failed
   */
  private void writeDecryptedFile(String path, InputStream inputStream) throws IOException {
    Path outputPath = Paths.get(path);
    try (FileOutputStream os = new FileOutputStream(outputPath.toFile())) {
      os.write(inputStream.readAllBytes());
    }
  }

  /**
   * Creates the correct cipher implementation
   *
   * @return cipher implementation
   * @throws FatalImplementationException if cipher is not supported
   */
  private PscCipher findFirstCipher() throws FatalImplementationException {
    Entry<String, Key> firstPrivate = user.getKeyChain().entrySet().stream()
        .filter(e -> !e.getKey().contains(KeyGenerator.PUBLIC_KEY_POSTFIX)).findFirst()
        .orElse(null);
    if (firstPrivate == null) {
      return new PlainTextCipher();
    }
    return CipherFactory.createCipher(firstPrivate.getValue().getType());
  }

}
