package ch.psc.domain.storage.service;

import ch.psc.domain.cipher.*;
import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.ProcessEvent;
import ch.psc.domain.storage.StorageManager;
import ch.psc.domain.user.User;
import ch.psc.exceptions.FatalImplementationException;
import org.bouncycastle.crypto.digests.ParallelHash;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.when;



import java.io.File;
import java.nio.file.*;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Walser
 */


public class StorageManagerTest {

    StorageManager storageManager;
    User testUser;
    File testFile;
    PscFile testPscFile;
    Consumer<ProcessEvent> testConsumer;
    LocalStorage localStorage;
    PscCipher cipher;
    private Map<String, Key> keyChain;
    File uploadFiles;
    File downloadFiles;
    Path testFileDirectory;

    @BeforeEach
    void setUp(){
        //folder uploadFiles erstellen
        testFileDirectory = Paths.get("src","test","testFiles");

        //user mock
        testUser = Mockito.mock(User.class);
        cipher = new AesCipher();
        ch.psc.domain.cipher.KeyGenerator keyGenerator = new KeyGenerator();
        try {
            keyChain = keyGenerator.generateKey(cipher.getKeyBits(), cipher.getAlgorithm());
        } catch (FatalImplementationException e) {
            e.printStackTrace();
        }
        when(testUser.getKeyChain()).thenReturn(keyChain);
        when(testUser.getDownloadPath()).thenReturn(testFileDirectory.toFile().getPath()+"\\downloadFiles");

        //testPscFile mock
        testPscFile = Mockito.mock(PscFile.class);
        when(testPscFile.getName()).thenReturn("test.txt.psc");
        when(testPscFile.getPath()).thenReturn(testFileDirectory.toFile().getPath()+"\\test.txt.psc");


        //testklasse
        storageManager = new StorageManager(testUser);

        //consumer Mock
        testConsumer = processEvent -> System.out.println("Consumer activated");

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
        deleteDir(uploadFiles);
    }

    @Test
    public void testDownloadFiles(){
        //folder downloadFiles erstellen
        testFile = new File(testFileDirectory.toFile().getAbsolutePath()+"\\test.txt.psc");

        downloadFiles = new File (testFileDirectory.toFile().getAbsolutePath()+"\\downloadFiles");
        if (downloadFiles.exists()){
            deleteDir(downloadFiles);
        }
        downloadFiles = new File (testFileDirectory.toFile().getAbsolutePath()+"\\downloadFiles");
        downloadFiles.mkdirs();

        localStorage = new LocalStorage(downloadFiles.getAbsolutePath());
        storageManager.downloadFiles(localStorage, testPscFile, testConsumer);

        //check if file exists
        assertTrue(fileExists(downloadFiles.getAbsolutePath()));

        //delete folder afterwards
        deleteDir(downloadFiles);


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
