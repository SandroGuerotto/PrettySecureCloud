package ch.psc.domain.storage.service;

import java.util.Map;

/**
 * Factory class to create {@link StorageService} based on given configuration.
 *
 * @author SandroGuerotto
 */
public class StorageServiceFactory {

    /**
     * Creates a new Services with the configuration of the logged-in user.
     * Example: creating a dropbox service with configuration of logged-in user.
     * <pre>
     * {@code
     * FileStorage dropbox = StorageServiceFactory.createService(
     *      StorageService.DROPBOX,
     *      user.getStorageServiceConfig().get(StorageService.DROPBOX));
     * }
     * </pre>
     *
     * @param service     desired service. {@link StorageService}
     * @param accountData configuration and access data
     * @return created and configured service
     * @throws IllegalArgumentException service unknown
     */
    public static FileStorage createService(StorageService service, Map<String, String> accountData) throws IllegalArgumentException {

        return switch (service) {
            case LOCAL -> createLocalStorageService(accountData);
            case DROPBOX -> createDropBoxService(accountData);
            case GOOGLE_DRIVE -> createGoogleDriveService(accountData);
            default -> throw new IllegalArgumentException("Service not supported");
        };

    }

    /**
     * Creates a Google Drive service
     *
     * @param accountData configuration and access data
     * @return Google Drive service
     */
    private static FileStorage createGoogleDriveService(Map<String, String> accountData) {
        return null; // TODO
    }

    /**
     * Creates a Dropbox service.
     *
     * @param accountData uses accountData to connect and update expired token
     * @return Dropbox service
     */
    private static FileStorage createDropBoxService(Map<String, String> accountData) {
        return DropBoxService.connect(accountData);
    }

    /**
     * Creates a local storage service.
     *
     * @param accountData uses path in the map
     * @return local storage service
     */
    private static FileStorage createLocalStorageService(Map<String, String> accountData) {
        return new LocalStorage(accountData.get("root_path"));
    }

}
