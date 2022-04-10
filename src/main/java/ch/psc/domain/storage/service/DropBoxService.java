package ch.psc.domain.storage.service;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.File;
import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class DropBoxService extends CloudService {

    public static final String DROPBOX_PSC_APP = "dropbox-psc.app";
    private DbxClientV2 client;

    public DropBoxService(DbxClientV2 client) {
        this();
        this.client = client;
    }

    public DropBoxService() {
        super("Dropbox");
    }

    @Override
    public List<Future<File>> upload(List<File> files) {
//    client.files().upload(files.get(1).getPath());
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Future<File>> download(List<File> files) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getAvailableStorageSpace() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Tree<File> getFileTree() {
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

    @Override
    public void login() {

    }


    public DbxRequestConfig getDbxRequestConfig() {
        return DbxRequestConfig.newBuilder("Pretty-Secure-Cloud")
                .withUserLocale("de_CH")
                .build();
    }

    private DbxAppInfo getDbxAppInfo() {
        try {
            return DbxAppInfo.Reader.readFromFile(DROPBOX_PSC_APP);
        } catch (JsonReader.FileLoadException e) {
            throw new RuntimeException("File " + DROPBOX_PSC_APP + " not found");
        }
    }

    public DbxWebAuth.Request buildAuthRequest() {
        return DbxWebAuth.newRequestBuilder()
                .withNoRedirect()
                .build();
    }

    public DbxWebAuth createDbxWebAuth() {
        return new DbxWebAuth(getDbxRequestConfig(), getDbxAppInfo());
    }


    public Map<String, String> finishFromCode(DbxWebAuth auth, String userCode) throws Exception {
        userCode = userCode.trim();
        DbxAuthFinish authFinish;
        try {
            authFinish = auth.finishFromCode(userCode);
        } catch (DbxException e) {
            e.printStackTrace();
            throw new Exception("Wrong code");
        }

        return Collections.singletonMap("access_token", authFinish.getAccessToken());
    }
}
