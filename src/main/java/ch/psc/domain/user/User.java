package ch.psc.domain.user;

import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.storage.service.StorageService;

import java.util.Map;

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

    public User(String username, String mail, String password, Map<StorageService, Map<String, String>> storageServiceConfig, Map<String, Key> keyChain) {
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.storageServiceConfig = storageServiceConfig;
        this.keyChain = keyChain;
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

    // Naming convention: "Verschlüsselungsmethode.public/private"
    public Key getKey(String keyName){
        return keyChain.get(keyName);
    }

    public Map<StorageService, Map<String, String>> getStorageServiceConfig() {
        return storageServiceConfig;
    }

    /**
     * Saves data to a JSON file
     */
    public void save() {
        new JSONWriterReader().writeToJson("user.json", this);
    }

}
