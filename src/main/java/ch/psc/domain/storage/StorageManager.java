package ch.psc.domain.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

public class StorageManager {

    private List<FileStorage> storageOptions;
    private final User user;
    private final ExecutorService executorService;

    public StorageManager(User user) {
        this.user = user;
        executorService = Executors.newCachedThreadPool();
    }

    public void uploadFiles(FileStorage storage, File file, Consumer<ProcessEvent> callback) {
        executorService.submit(() -> {
            try {
                callback.accept(ProcessEvent.ENCRYPTING);
                PscFile encrypted = encrypt(file);
                File temp = Files.createTempFile(encrypted.getName(), ".psc").toFile();
                try(FileOutputStream os = new FileOutputStream(temp)) {
                  if(encrypted.getNonce() != null) {
                    os.write(encrypted.getNonce());
                  }
                  os.write(encrypted.getData());
                }
                callback.accept(ProcessEvent.ENCRYPTED);
                callback.accept(ProcessEvent.UPLOADING);
                storage.upload(encrypted, new FileInputStream(temp));
                callback.accept(ProcessEvent.UPLOADED);
            } catch (IOException | FatalImplementationException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            callback.accept(ProcessEvent.FINISHED);
        });
    }

    public void downloadFiles(FileStorage storage, PscFile file, Consumer<ProcessEvent> callback) {
        executorService.submit(() -> {
            callback.accept(ProcessEvent.DOWNLOADING);
            InputStream inputStream = storage.download(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            
            try {
                Path outputPath = Paths.get(user.getDownloadPath() + file.getName().replace(PscFile.PSC_FILE_EXTENSION, ""));
                try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath.toFile())) {
                  fileOutputStream.write(bufferedInputStream.readAllBytes());
                }
                callback.accept(ProcessEvent.DOWNLOADED);
    
                callback.accept(ProcessEvent.DECRYPTING);
                decrypt(file, outputPath);
                callback.accept(ProcessEvent.DECRYPTED);
            } catch (IOException | FatalImplementationException | InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }

            callback.accept(ProcessEvent.FINISHED);
        });
    }

    public void loadManagedFiles(FileStorage storage, String path, Consumer<List<PscFile>> callback) {
        if (storage != null) {
            executorService.submit(() -> {
                callback.accept(storage.getFiles(path));
            });
        }
    }

    public List<FileStorage> getStorageOptions() {
        return storageOptions;
    }

    public void loadStorageServices() {
        storageOptions = user.getStorageServiceConfig()
                .entrySet().stream()
                .map(entry -> StorageServiceFactory.createService(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        try {
            AuthenticationContext.getAuthService().update(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }
    
    private PscFile encrypt(File unencrypted) throws FileNotFoundException, IOException, FatalImplementationException, InterruptedException, ExecutionException {
      PscFile pscFile = new PscFile(unencrypted.getName() + PscFile.PSC_FILE_EXTENSION, unencrypted.getPath(),EncryptionState.DECRYPTED , unencrypted.length(), null, false);
      try(FileInputStream is = new FileInputStream(unencrypted)) {
        pscFile.setData(is.readAllBytes());
      }
      PscCipher cipher = findFirstCipher();
      List<Future<PscFile>> futureFiles = cipher.encrypt(cipher.findEncryptionKey(user.getKeyChain()), Arrays.asList(pscFile));
      PscFile encrypted = futureFiles.get(0).get();
      return encrypted;
    }
    
    private void decrypt(PscFile file, Path ioPath) throws FatalImplementationException, FileNotFoundException, IOException, InterruptedException, ExecutionException {
      PscCipher cipher = findFirstCipher();
      PscFile pscFile = new PscFile(file.getName(), file.getPath(), EncryptionState.ENCRYPTED, file.getFileSize(), null, false);
      byte[] fileContent = null;
      try(FileInputStream is = new FileInputStream(ioPath.toFile()) ) {
        fileContent = is.readAllBytes();
      }

      assert(fileContent != null);
      assert(fileContent.length > 0);
      byte[] nonce = new byte[cipher.getNonceLength()];
      byte[] data = new byte[fileContent.length - cipher.getNonceLength()];
      System.arraycopy(fileContent, 0, nonce, 0, cipher.getNonceLength());
      System.arraycopy(fileContent, cipher.getNonceLength(), data, 0, fileContent.length - cipher.getNonceLength());
      pscFile.setData(data);
      pscFile.setNonce(nonce);
      
      List<Future<PscFile>> decFiles = cipher.decrypt(cipher.findDecryptionKey(user.getKeyChain()), Arrays.asList(pscFile));
      PscFile decrypted = decFiles.get(0).get();
      try(FileOutputStream os = new FileOutputStream(ioPath.toFile())) {
        os.write(decrypted.getData());
      }
    }
    
    private PscCipher findFirstCipher() throws FatalImplementationException {
      Entry<String, Key> firstPrivate = user.getKeyChain().entrySet().stream()
          .filter(e -> e.getKey().contains(KeyGenerator.PUBLIC_KEY_POSTFIX)==false)
          .findFirst().orElse(null);
      if(firstPrivate == null) {
        return new PlainTextCipher();
      }
      return CipherFactory.createCipher(firstPrivate.getValue().getType());
    }

}
