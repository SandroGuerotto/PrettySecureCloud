package ch.psc.domain.storage;

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

    public void uploadFiles(FileStorage storage, File file) {
        executorService.submit(()->{
            try {
                // todo encrypt
                storage.upload(new PscFile(file.getPath(), file.getName(), file.length(), null, false), new FileInputStream(file) );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public List<Future<PscFile>> downloadFiles(FileStorage storage,PscFile file) {
        if (storage!=null){
            executorService.submit(() -> {
                InputStream inputStream = storage.download(file);
                //TODO decrypt
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                try (FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.home")+"\\Downloads\\"+file.getName())) {
                    fileOutputStream.write(bufferedInputStream.readAllBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return null;
    }

    public void loadManagedFiles(FileStorage storage, String path, Consumer<List<PscFile>> callback) {
        if (storage!=null){
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
    }

    public User getUser() {
        return user;
    }

}
