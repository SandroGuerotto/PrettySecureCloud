package ch.psc.domain.user;

import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.exceptions.AuthenticationException;
import ch.psc.exceptions.UpdateUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link JSONAuthenticationService}.
 *
 * @author SandroGuerotto
 */
class JSONAuthenticationServiceTest {

    private JSONAuthenticationService cut;
    private User expUser;

    @Mock
    private JSONWriterReader jsonWriterReaderMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cut = new JSONAuthenticationService(jsonWriterReaderMock);

        expUser = new User("test", "mail", "pwd",
                Collections.singletonMap(StorageService.DROPBOX, Collections.singletonMap("token", "abc")),
                Collections.singletonMap("Test", new Key(new SecretKeySpec("fooBarBaz".getBytes(), "TestAlgo"))), "path");
    }

    @Test
    void authenticateSuccessful() {

        fakeRead();
        try {
            User act = cut.authenticate("mail", "pwd");
            assertEquals(expUser.getUsername(), act.getUsername());
            assertEquals(expUser.getUsername(), act.getUsername());
            assertEquals(expUser.getPassword(), act.getPassword());
            assertEquals(expUser.getStorageServiceConfig(), act.getStorageServiceConfig());
            assertKeyChain(expUser.getKeyChain(), act.getKeyChain());

        } catch (AuthenticationException e) {
            fail(e.getMessage());
        }

    }

    @Test
    void authenticateNotSuccessful() {

        fakeRead();
        try {
            cut.authenticate("a", "a");
            fail("Fail expected");

        } catch (AuthenticationException e) {
            assertNotNull(e);
        }

    }

    private void fakeRead() {
        try {
            when(jsonWriterReaderMock.readFromJson(anyString(), any())).thenReturn(
                    new JSONAuthenticationService.JSONUser("test", "mail", "pwd",
                            Collections.singletonMap(StorageService.DROPBOX, Collections.singletonMap("token", "abc")),
                            Collections.singletonMap("Test", Map.of("secret", "fooBarBaz", "algorithm", "TestAlgo")),
                            "path"));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void updateUser() {
        when(jsonWriterReaderMock.writeToJson(anyString(), any())).thenReturn(true);
        fakeRead();
        try {

            User act = cut.update(expUser);
            assertEquals(expUser.getUsername(), act.getUsername());
            assertEquals(expUser.getUsername(), act.getUsername());
            assertEquals(expUser.getPassword(), act.getPassword());
            assertEquals(expUser.getStorageServiceConfig(), act.getStorageServiceConfig());
            assertKeyChain(expUser.getKeyChain(), act.getKeyChain());

        } catch (UpdateUserException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void signupSuccessful() {

        when(jsonWriterReaderMock.writeToJson(anyString(), any())).thenReturn(true);
        fakeRead();
        try {

            User act = cut.signup(expUser);
            assertEquals(expUser.getUsername(), act.getUsername());
            assertEquals(expUser.getUsername(), act.getUsername());
            assertEquals(expUser.getPassword(), act.getPassword());
            assertEquals(expUser.getStorageServiceConfig(), act.getStorageServiceConfig());
            assertKeyChain(expUser.getKeyChain(), act.getKeyChain());

        } catch (AuthenticationException e) {
            fail(e.getMessage());
        }
    }

    private void assertKeyChain(Map<String, Key> expChain, Map<String, Key> actChain) {
        actChain.forEach(
                (s, key) -> {
                    java.security.Key exp = expChain.get(s).getKey();
                    java.security.Key act = key.getKey();
                    assertEquals(exp.getAlgorithm(), act.getAlgorithm());
                    assertEquals(new String(exp.getEncoded()), new String(act.getEncoded()));
                }
        );
    }
}