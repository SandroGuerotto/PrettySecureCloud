package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.StorageManager;
import ch.psc.domain.user.User;
import ch.psc.exceptions.FatalImplementationException;
import org.bouncycastle.crypto.digests.ParallelHash;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Walser
 */


public class StorageManagerTest {

    StorageManager storageManager;
    FileStorage fileStorage;
    User testUser;
    File testFile;
    PscFile testPscFile;

    @BeforeEach
    void setUp(){
        //folder uploadFiles erstellen
        testFileDirectory = Paths.get("src","test","testFiles");

        //user mock
        testUser = Mockito.mock(User.class);
        testFile = Mockito.mock(File.class);
        testPscFile = Mockito.mock(PscFile.class);
        fileStorage = Mockito.mock(FileStorage.class);
        storageManager = new StorageManager(testUser);
    }

    @Test
    public void testUploadFiles() {
        //folder uploadFiles erstellen
        testFile = new File(testFileDirectory.toFile().getAbsolutePath()+"\\test.txt");
        uploadFiles = new File (testFileDirectory.toFile().getAbsolutePath()+"\\uploadFiles");
        if (uploadFiles.exists()){
            deleteDir(uploadFiles);
        }
        uploadFiles.mkdirs();

        //local storage erstellen
        localStorage = new LocalStorage(uploadFiles.getAbsolutePath());
        storageManager.uploadFiles(localStorage, testFile, testConsumer);

        //check if file exists
        assertTrue(fileExists(uploadFiles.getAbsolutePath()));

        //delete folder afterwards
        //deleteDir(uploadFiles);
    }

    @Test
    public void testDownloadFiles(){

    }

    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    private boolean fileExists (String filePath){
        File f = new File(filePath);
        return f.exists();
    }

}
