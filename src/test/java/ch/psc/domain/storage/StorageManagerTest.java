package ch.psc.domain.storage;

import ch.psc.domain.cipher.AesCipher;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.cipher.KeyGenerator;
import ch.psc.domain.cipher.PscCipher;
import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.service.LocalStorage;
import ch.psc.domain.user.User;
import ch.psc.exceptions.FatalImplementationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
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
    void setUp() {
        //folder uploadFiles erstellen
        testFileDirectory = Paths.get("src", "test", "resources");

        //user mock
        testUser = Mockito.mock(User.class);
        cipher = new AesCipher();
        ch.psc.domain.cipher.KeyGenerator keyGenerator = new KeyGenerator();
        try {
            keyChain = keyGenerator.generateKey(cipher.getKeyBits(), cipher.getAlgorithm());
        } catch (FatalImplementationException e) {
            fail(e.getMessage());
        }
        when(testUser.getKeyChain()).thenReturn(keyChain);
        when(testUser.getDownloadPath()).thenReturn(testFileDirectory.toFile().getPath());

        //testPscFile mock
        testPscFile = Mockito.mock(PscFile.class);
        when(testPscFile.getName()).thenReturn("test.txt.psc");
        when(testPscFile.getPath()).thenReturn(testFileDirectory.toFile().getPath() + "\\test.txt.psc");


        //testklasse
        storageManager = new StorageManager(testUser);

        //consumer Mock
        AtomicInteger eventCounter = new AtomicInteger(0);
        testConsumer = processEvent -> {
            eventCounter.set(eventCounter.get() + 1);
            if (processEvent.equals(ProcessEvent.FINISHED))
                assertEquals(5, eventCounter.get());
        };

    }

    @Test
    public void testUploadFiles() {
        //folder uploadFiles erstellen
        testFile = new File(testFileDirectory.toFile().getAbsolutePath() + "\\test.txt");
        uploadFiles = new File(testFileDirectory.toFile().getAbsolutePath());


        //local storage erstellen
        localStorage = new LocalStorage(uploadFiles.getAbsolutePath());
        storageManager.uploadFiles(localStorage, testFile, testConsumer);

        //check if file exists
        assertTrue(fileExists(uploadFiles.getAbsolutePath()));

    }

    @Test
    public void testDownloadFiles() {
        //folder downloadFiles erstellen
        testFile = new File(testFileDirectory.toFile().getAbsolutePath() + "\\test.txt.psc");

        downloadFiles = new File(testFileDirectory.toFile().getAbsolutePath());

        localStorage = new LocalStorage(downloadFiles.getAbsolutePath());
        storageManager.downloadFiles(localStorage, testPscFile, testConsumer);

        //check if file exists
        assertTrue(fileExists(downloadFiles.getAbsolutePath()));
    }


    private boolean fileExists(String filePath) {
        File f = new File(filePath);
        return f.exists();
    }

}
