package ch.psc.domain.user;

import java.util.Map;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.storage.service.StorageService;

/**
 * Holds all data of logged in user.
 *
 * @author SandroGuerotto
 */
public class User {

  private final String username;
  private final String mail;
  private final String password;
  private final Map<StorageService, Map<String, String>> storageServiceConfig;
  private final Map<String, Key> keyChain;
  private final String downloadPath;

  public User(String username, String mail, String password,
      Map<StorageService, Map<String, String>> storageServiceConfig, Map<String, Key> keyChain,
      String downloadPath) {
    this.username = username;
    this.mail = mail;
    this.password = password;
    this.storageServiceConfig = storageServiceConfig;
    this.keyChain = keyChain;
    this.downloadPath = downloadPath;
  }

  public String getUsername() {
    return username;
  }

  public String getMail() {
    return mail;
  }

  public String getPassword() {
    return password;
  }

  public Key getKey(String keyName) {
    return keyChain.get(keyName);
  }

  public Map<StorageService, Map<String, String>> getStorageServiceConfig() {
    return storageServiceConfig;
  }

  public Map<String, Key> getKeyChain() {
    return keyChain;
  }

  public String getDownloadPath() {
    return downloadPath;
  }
}
