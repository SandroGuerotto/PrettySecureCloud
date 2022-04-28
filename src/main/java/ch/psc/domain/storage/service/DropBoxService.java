package ch.psc.domain.storage.service;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.PscFile;
import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.SpaceUsage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Wrapper for Dropbox requests. Handles all communication with Dropbox server.
 *
 * @author SandroGuerotto
 */
public class DropBoxService extends CloudService {

    private static final String DROPBOX_PSC_APP = "configs/dropbox-psc.app";
    public static final String PRETTY_SECURE_CLOUD = "Pretty-Secure-Cloud";
    private DbxClientV2 client;

    /**
     * Create a new instance for Dropbox all communication.
     * @param client Dropbox client with valid access token
     */
    public DropBoxService(DbxClientV2 client) {
        this();
        this.client = client;
    }

    /**
     * Create a new instance for Dropbox communication.
     * Only used for getting an access token.
     */
    public DropBoxService() {
        super("Dropbox");
    }

    @Override
    public List<Future<PscFile>> upload(List<PscFile> files) {
        try { // todo pro file: evtl besser nur immer ein file als import und loop ausserhalb
            client.files().upload(files.get(0).getPath());

        } catch (DbxException e) {
            e.printStackTrace(); // todo error handling
        }
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
            return spaceUsage.getAllocation().getIndividualValue().getAllocated() - spaceUsage.getUsed();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Tree<PscFile> getFileTree() {
        try {
            ListFolderResult result = client.files().listFolder("");
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    System.out.println(metadata.getPathLower());
                }

                if (!result.getHasMore()) {
                    break;
                }

                result = client.files().listFolderContinue(result.getCursor());
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return null;
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
     * @return Dropbox authorization request
     */
    public DbxWebAuth.Request buildAuthRequest() {
        return DbxWebAuth.newRequestBuilder().withNoRedirect().build();
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
            return Collections.singletonMap("access_token", authFinish.getAccessToken());
        } catch (DbxException e) {
            e.printStackTrace(); // TODO error handling
            throw new Exception("Wrong code");
        }

    }
}
