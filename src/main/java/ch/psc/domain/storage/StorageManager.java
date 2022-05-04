package ch.psc.domain.storage;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.service.FileStorage;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.domain.storage.service.StorageServiceFactory;
import ch.psc.domain.user.User;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StorageManager {

    private Tree<PscFile> managedFiles;
    private List<FileStorage> storageOptions;
    private final User user;
    private final ExecutorService executorService;

    public StorageManager(User user) {
        this.user = user;
        executorService = Executors.newCachedThreadPool();
    }

    public void uploadFiles(FileStorage storage,List<PscFile> files) {
        storage.upload(files);
        //TODO
    }

    public List<Future<PscFile>> downloadFiles(FileStorage storage,List<PscFile> files,Method callback) {
        //TODO
        return null;
    }

    public void loadManagedFiles(FileStorage storage, String path, Consumer<List<PscFile>> callback) {
        if (storage!=null){
            executorService.submit(() -> {
                callback.accept(storage.getFiles(path));
            });

        }
//        return managedFiles;
    }

    public List<FileStorage> getStorageOptions() {
        return storageOptions;
    }

    public void addNewStorage(StorageService storageService, Map<String, String> config) {
        user.getStorageServiceConfig()
                .put(storageService, config);
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
