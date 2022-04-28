package ch.psc.domain.user;

import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.exceptions.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Sandro
 */
class JSONAuthenticationServiceTest {

    private JSONAuthenticationService cut;
    @Mock
    private JSONWriterReader jsonWriterReaderMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cut = new JSONAuthenticationService(jsonWriterReaderMock);
    }

    @Test
    void authenticateSuccessful() {
        User exp = new User("test", "a", "a",
                Collections.singletonMap(StorageService.DROPBOX, Collections.singletonMap("token", "abc")),
                Collections.singletonMap("Test", new Key(new SecretKeySpec("fooBarBaz".getBytes(), "TestAlgo"))));
        try {
            User act = cut.authenticate("a", "a");
            assertEquals(exp.getUsername(), act.getUsername());
            assertEquals(exp.getUsername(), act.getUsername());
            assertEquals(exp.getPassword(), act.getPassword());
            assertEquals(exp.getStorageServiceConfig(), act.getStorageServiceConfig());
            assertEquals(exp.getKeyChain(), act.getKeyChain());

        } catch (AuthenticationException e) {
            fail(e.getMessage());
        }

    }

    @Test
    void signupSuccessful() throws IOException {
        User exp = new User("name", "mail", "pwd", new HashMap<>(), new HashMap<>());

        when(jsonWriterReaderMock.writeToJson(anyString(), any())).thenReturn(true);
        when(jsonWriterReaderMock.readFromJson(anyString(), any())).thenReturn(
                new JSONAuthenticationService.JSONUser("name", "mail", "pwd", new HashMap<>()
        ));
        try {

            User act = cut.signup(exp);
            assertEquals(exp.getUsername(), act.getUsername());
            assertEquals(exp.getUsername(), act.getUsername());
            assertEquals(exp.getPassword(), act.getPassword());

        } catch (AuthenticationException e) {
            fail(e.getMessage());
        }
    }
}