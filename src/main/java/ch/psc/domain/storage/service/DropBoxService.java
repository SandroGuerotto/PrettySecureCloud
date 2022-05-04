package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.SpaceUsage;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Wrapper for Dropbox requests. Handles all communication with Dropbox server.
 *
 * @author SandroGuerotto
 */
public class DropBoxService implements FileStorage {

    private static final String DROPBOX_PSC_APP = "configs/dropbox-psc.app";
    public static final String PRETTY_SECURE_CLOUD = "Pretty-Secure-Cloud";
    private DbxClientV2 client;
    private final String name;
    private final StringProperty currentPathProperty = new SimpleStringProperty(ROOT_DIR);
    private final SimpleDoubleProperty usedStorageSpaceProperty = new SimpleDoubleProperty();
    private static final String ROOT_DIR = "";

    /**
     * Create a new instance for Dropbox all communication.
     *
     * @param client Dropbox client with valid access token
     */
    public DropBoxService(DbxClientV2 client) {
        this.client = client;
        this.name = "Dropbox";
    }

    /**
     * Create a new instance for Dropbox communication.
     * Only used for getting an access token.
     */
    public DropBoxService() {
        this.name = "Dropbox";
    }

    @Override
    public List<Future<PscFile>> upload(List<PscFile> files) {
        System.out.println("upload in:" + currentPathProperty.get());
        files.forEach(System.out::println);
//        try { // todo pro file: evtl besser nur immer ein file als import und loop ausserhalb
//            client.files().upload(files.get(0).getPath());
//
//        } catch (DbxException e) {
//            e.printStackTrace(); // todo error handling
//        }
        return null;
    }

    @Override
    public List<Future<PscFile>> download(List<PscFile> files) {
        try {
            client.files().download(files.get(0).getPath());
        } catch (DbxException e) {
            e.printStackTrace(); // todo error handling
        }
        return null;
    }

    @Override
    public double getAvailableStorageSpace() {
        try {
            SpaceUsage spaceUsage = client.users().getSpaceUsage();
            long spaceInBytes = spaceUsage.getAllocation().getIndividualValue().getAllocated() - spaceUsage.getUsed();
            usedStorageSpaceProperty.set(spaceUsage.getUsed());
            return spaceInBytes / 1024.0 / 1024.0 / 1024.0;
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getTotalStorageSpace() {
        try {
            SpaceUsage spaceUsage = client.users().getSpaceUsage();
            return spaceUsage.getAllocation().getIndividualValue().getAllocated() / 1024.0 / 1024.0 / 1024.0;
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<PscFile> getFiles(String path) {
        currentPathProperty.set(path);
        ArrayList<PscFile> list = new ArrayList<>();
        try {
            ListFolderResult result = client.files().listFolder(currentPathProperty.get());
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    list.add(new PscFile(metadata.getName(), metadata.getPathLower(), metadata instanceof FolderMetadata));
                }

                if (!result.getHasMore()) {
                    break;
                }

                result = client.files().listFolderContinue(result.getCursor());
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DoubleProperty getUsedStorageSpaceProperty() {
        return usedStorageSpaceProperty;
    }

    @Override
    public String getRoot() {
        return ROOT_DIR;
    }

    /**
     * Creates a new Dropbox configuration.
     * Default parameter: clientID=Pretty-Secure-Cloud, Locale=de_CH
     *
     * @return new DbxRequestConfig instance
     */
    public DbxRequestConfig getDbxRequestConfig() {
        return DbxRequestConfig.newBuilder(PRETTY_SECURE_CLOUD).withUserLocale("de_CH").build();
    }

    private DbxAppInfo getDbxAppInfo() {
        try {
            return DbxAppInfo.Reader.readFromFile(DROPBOX_PSC_APP);
        } catch (JsonReader.FileLoadException e) {
            throw new RuntimeException("File " + DROPBOX_PSC_APP + " not found");
        }
    }

    /**
     * Creates an authorization request to access the user's data without a redirect URL.
     *
     * @return Dropbox authorization request
     */
    public DbxWebAuth.Request buildAuthRequest() {
        return DbxWebAuth.newRequestBuilder().withTokenAccessType(TokenAccessType.OFFLINE)
                .withNoRedirect().build();
    }

    /**
     * Creates OAuth Dropbox request configuration
     *
     * @return OAuth Dropbox request configuration
     */
    public DbxWebAuth createDbxWebAuth() {
        return new DbxWebAuth(getDbxRequestConfig(), getDbxAppInfo());
    }

    /**
     * Called  after the user has visited the authorization URL and copy/pasted the authorization code that Dropbox gave them.
     *
     * @param auth     OAuth request configuration
     * @param userCode The authorization code shown to the user when they clicked "Allow" on the authorization, page on the Dropbox website.
     * @return a Map with OAuth access token used for authorization with Dropbox servers. Use key "access_token" to get the token.
     * @throws Exception if wrong code was entered
     */
    public Map<String, String> finishFromCode(DbxWebAuth auth, final String userCode) throws Exception {
        try {
            DbxAuthFinish authFinish = auth.finishFromCode(userCode.trim());

            return Map.of("access_token", authFinish.getAccessToken(),
                    "refresh_token", authFinish.getRefreshToken());
        } catch (DbxException e) {
            e.printStackTrace(); // TODO error handling
            throw new Exception("Wrong code");
        }

    }
}
