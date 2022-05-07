package ch.psc.domain.storage;

import ch.psc.domain.common.context.AuthenticationContext;
import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.service.FileStorage;
import ch.psc.domain.storage.service.StorageServiceFactory;
import ch.psc.domain.user.User;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
                // todo encrypt
                callback.accept(ProcessEvent.ENCRYPTED);
                callback.accept(ProcessEvent.UPLOADING);
                storage.upload(new PscFile(file.getPath(), file.getName(), file.length(), null, false), new FileInputStream(file));
                callback.accept(ProcessEvent.UPLOADED);
                callback.accept(ProcessEvent.FINISHED);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public List<Future<PscFile>> downloadFiles(FileStorage storage, PscFile file, Consumer<ProcessEvent> callback) {
        if (storage != null) {
            executorService.submit(() -> {
                callback.accept(ProcessEvent.DOWNLOADING);
                InputStream inputStream = storage.download(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                try (FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.home") + "\\Downloads\\" + file.getName())) {
                    fileOutputStream.write(bufferedInputStream.readAllBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.accept(ProcessEvent.DOWNLOADED);

                callback.accept(ProcessEvent.DECRYPTING);
                //TODO decrypt
                callback.accept(ProcessEvent.DECRYPTED);

                callback.accept(ProcessEvent.FINISHED);
            });
        }
        return null;
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

}
