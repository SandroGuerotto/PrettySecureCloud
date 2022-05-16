package ch.psc.domain.cipher;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.domain.user.JSONAuthenticationService;
import ch.psc.domain.user.User;
import ch.psc.exceptions.AuthenticationException;
import ch.psc.exceptions.FatalImplementationException;

public class IntegrationTest {

  private static final String SECRET_MESSAGE = "Super Secret Message B-)";

  private JSONAuthenticationService authService;
  private User userData;

  @BeforeEach
  public void setup() {
    authService = new JSONAuthenticationService(new JSONWriterReader());
    Map<StorageService, Map<String, String>> services = new HashMap<>();
    services.put(StorageService.DROPBOX, Collections.singletonMap("token", "abc"));
    userData = new User("foo", "bar", "baz", services, new HashMap<String, Key>(), "download");
  }

  @Test
  public void aesIntegrationTest() throws FatalImplementationException, AuthenticationException,
      InterruptedException, ExecutionException {
    AesCipher cipher = new AesCipher();
    Map<String, Key> keyChain = cipher.generateKey();

    userData.getKeyChain().putAll(keyChain);
    authService.signup(userData);
    User actUser = authService.authenticate(userData.getMail(), userData.getPassword());

    assertKeyEquals(userData.getKey(cipher.getAlgorithm()), actUser.getKey(cipher.getAlgorithm()));
    assertEncryptDecrypt(cipher, actUser.getKeyChain());
  }

  @Test
  public void rsaIntegragionTest() throws FatalImplementationException, AuthenticationException,
      InterruptedException, ExecutionException {
    RsaCipher cipher = new RsaCipher();
    Map<String, Key> keyChain = cipher.generateKey();

    userData.getKeyChain().putAll(keyChain);
    authService.signup(userData);
    User actUser = authService.authenticate(userData.getMail(), userData.getPassword());

    assertKeyEquals(userData.getKey(cipher.getAlgorithm()), actUser.getKey(cipher.getAlgorithm()));
    assertKeyEquals(userData.getKey(cipher.getAlgorithm() + KeyGenerator.PUBLIC_KEY_POSTFIX),
        actUser.getKey(cipher.getAlgorithm() + KeyGenerator.PUBLIC_KEY_POSTFIX));
    assertEncryptDecrypt(cipher, actUser.getKeyChain());
  }

  private void assertKeyEquals(Key expected, Key actual) {
    assertEquals(expected.getType(), actual.getType());
    assertArrayEquals(expected.getKey().getEncoded(), expected.getKey().getEncoded());
  }

  private void assertEncryptDecrypt(PscCipher cipher, Map<String, Key> keyChain)
      throws InterruptedException, ExecutionException {
    PscFile file = new PscFile();
    file.setData(SECRET_MESSAGE.getBytes());

    List<Future<PscFile>> encFiles =
        cipher.encrypt(cipher.findEncryptionKey(keyChain), Arrays.asList(file));
    List<Future<PscFile>> decFiles =
        cipher.decrypt(cipher.findDecryptionKey(keyChain), Arrays.asList(encFiles.get(0).get()));
    String decryptedMessage = new String(decFiles.get(0).get().getData());

    assertEquals(SECRET_MESSAGE, decryptedMessage);
  }
}
