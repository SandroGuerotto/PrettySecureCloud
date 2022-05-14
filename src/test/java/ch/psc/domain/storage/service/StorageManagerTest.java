package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.StorageManager;
import ch.psc.domain.user.User;
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
        testUser = Mockito.mock(User.class);
        testFile = Mockito.mock(File.class);
        testPscFile = Mockito.mock(PscFile.class);
        fileStorage = Mockito.mock(FileStorage.class);
        storageManager = new StorageManager(testUser);
    }

    @Test
    public void testUploadFiles(){

    }

    @Test
    public void testDownloadFiles(){

    }

    @Test
    public void testLoadStorageServices(){

    }
}
