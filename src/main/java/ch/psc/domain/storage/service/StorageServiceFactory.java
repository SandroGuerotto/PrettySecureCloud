package ch.psc.domain.storage.service;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.util.Map;

public class StorageServiceFactory {

    public static FileStorage createService(StorageService service, Map<String, String> accountData) throws  IllegalArgumentException{

        return switch (service) {
            case LOCAL -> createLocalStorageService(accountData);
            case DROPBOX -> createDropBoxService(accountData);
            case GOOGLE_DRIVE -> createGoogleDriveService(accountData);
            default -> throw new IllegalArgumentException("Service not supported");
        };

    }

    private static FileStorage createGoogleDriveService(Map<String, String> accountData) {
        return null;
    }

    private static FileStorage createDropBoxService(Map<String, String> accountData) {
        DbxRequestConfig config = new DropBoxService().getDbxRequestConfig();
        return new DropBoxService(new DbxClientV2(config, accountData.get("access_token")));
    }


    private static FileStorage createLocalStorageService(Map<String, String> accountData) {
        return null;
    }

}
