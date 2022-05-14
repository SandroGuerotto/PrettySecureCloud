package ch.psc.domain.storage.service;

import ch.psc.domain.file.EncryptionState;
import ch.psc.domain.file.PscFile;
import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.oauth.DbxRefreshResult;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.SpaceUsage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for Dropbox requests. Handles all communication with Dropbox server.
 *
 * @author SandroGuerotto
 */
public class DropBoxService implements FileStorage {

    private static final String DROPBOX_PSC_APP = "configs/dropbox-psc.app";
    public static final String PRETTY_SECURE_CLOUD = "Pretty-Secure-Cloud";
    private static final String FILE_SEPARATOR = "/";
    private DbxClientV2 client;
    private final String name;
    private String currentPath = ROOT_DIR;
    private final SimpleObjectProperty<BigDecimal> usedStorageSpaceProperty = new SimpleObjectProperty<>();
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

    public static DropBoxService connect(Map<String, String> accountData) {
        DbxRequestConfig config = getDbxRequestConfig();
        DbxAppInfo dbxAppInfo = getDbxAppInfo();
        DbxCredential dbxCredential = new DbxCredential(
                accountData.get("access_token"), Long.decode(accountData.get("expires_at")), accountData.get("refresh_token"), dbxAppInfo.getKey(), dbxAppInfo.getSecret());
        try {
            DbxRefreshResult refresh = dbxCredential.refresh(config);
            accountData.put("access_token", refresh.getAccessToken());
            accountData.put("expires_at", refresh.getExpiresAt().toString());
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return new DropBoxService(new DbxClientV2(config, accountData.get("access_token")));
    }

    @Override
    public boolean upload(PscFile file, InputStream inputStream) {
        try {
            client.files().upload(currentPath + "/" + file.getName()).uploadAndFinish(inputStream);
            return true;
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public InputStream download(PscFile file) {
        try {
            return client.files().download(file.getPath()).getInputStream();
        } catch (DbxException e) {
            e.printStackTrace(); // todo error handling
        }
        return null;
    }

    @Override
    public BigDecimal getUsedStorageSpace() {
        try {
            SpaceUsage spaceUsage = client.users().getSpaceUsage();
            usedStorageSpaceProperty.set(new BigDecimal(spaceUsage.getUsed()));
            return new BigDecimal(spaceUsage.getUsed());
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    @Override
    public BigDecimal getTotalStorageSpace() {
        try {
            SpaceUsage spaceUsage = client.users().getSpaceUsage();
            return new BigDecimal(spaceUsage.getAllocation().getIndividualValue().getAllocated());
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    @Override
    public List<PscFile> getFiles(final String path) {
        currentPath = path;
        ArrayList<PscFile> list = new ArrayList<>();
        try {
            ListFolderResult result = client.files().listFolder(currentPath);
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    PscFile file;

                    if (metadata instanceof FileMetadata fileMetadata) {
                        file = new PscFile(metadata.getName(), metadata.getPathLower(),EncryptionState.ENCRYPTED , fileMetadata.getSize(), fileMetadata.getClientModified(), false);
                    } else {
                        file = new PscFile(metadata.getName(), metadata.getPathLower(), EncryptionState.ENCRYPTED, 0, null, true);
                    }
                    list.add(file);
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
    public ObjectProperty<BigDecimal> getUsedStorageSpaceProperty() {
        return usedStorageSpaceProperty;
    }

    @Override
    public String getRoot() {
        return ROOT_DIR;
    }

    @Override
    public String getSeparator() {
        return FILE_SEPARATOR;
    }

    /**
     * Creates a new Dropbox configuration.
     * Default parameter: clientID=Pretty-Secure-Cloud, Locale=de_CH
     *
     * @return new DbxRequestConfig instance
     */
    private static DbxRequestConfig getDbxRequestConfig() {
        return DbxRequestConfig.newBuilder(PRETTY_SECURE_CLOUD).withUserLocale("de_CH").build();
    }

    /**
     * Reads the dropbox application configuration file.
     * The credentials are need to identify the psc-application to use dropbox services
     *
     * @return Credentials about the psc-application
     */
    private static DbxAppInfo getDbxAppInfo() {
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
                    "refresh_token", authFinish.getRefreshToken(),
                    "expires_at", authFinish.getExpiresAt().toString());
        } catch (DbxException e) {
            throw new Exception("Wrong code");
        }

    }
}
