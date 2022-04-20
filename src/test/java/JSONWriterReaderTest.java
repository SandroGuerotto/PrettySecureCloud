import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class JSONWriterReaderTest {

    private JSONWriterReader cut;
    private User userData;
    private final static String FILE_PATH = "src/test/resources/user.json";

    @BeforeEach
    void setup() {
        cut = new JSONWriterReader();
        Map<StorageService, Map<String, String>> services = new HashMap<>();
        services.put(StorageService.DROPBOX, Collections.singletonMap("token", "abc"));
        userData = new User("testname", "mail", "password", services);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(FILE_PATH));
    }

    @Test
    void writePlayerData() {
        assertTrue(cut.writeToJson(FILE_PATH, userData));
    }

    @Test
    void readPlayerData() throws IOException {
        userData.getStorageServiceConfig()
                .put(StorageService.LOCAL, Collections.singletonMap("path", "/path/test"));
        writePlayerData();

        User act = cut.readFromJson(FILE_PATH, User.class);

        assertEquals(userData.getMail(), act.getMail());
        assertEquals(userData.getUsername(), act.getUsername());
        assertEquals(userData.getPassword(),act.getPassword());
        assertEquals(Collections.singletonMap("path", "/path/test"),act.getStorageServiceConfig().get(StorageService.LOCAL));
        assertEquals(Collections.singletonMap("token", "abc"),act.getStorageServiceConfig().get(StorageService.DROPBOX));
    }
}
