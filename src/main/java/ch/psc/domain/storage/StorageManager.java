package ch.psc.domain.storage;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.File;
import ch.psc.domain.storage.service.FileStorage;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.domain.storage.service.StorageServiceFactory;
import ch.psc.domain.user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class StorageManager {

    private Tree<File> managedFiles;
    private List<FileStorage> storageOptions;
    private final User user;

    public StorageManager(User user) {
        this.user = user;
    }

    public void uploadFiles(List<File> files) {
        //TODO
    }

    public List<Future<File>> downloadFiles(List<File> files) {
        //TODO
        return null;
    }

    public Tree<File> getManagedFiles() {
        return managedFiles;
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
