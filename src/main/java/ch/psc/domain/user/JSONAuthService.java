package ch.psc.domain.user;

import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.exceptions.AuthenticationException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sandro
 */
public class JSONAuthService implements AuthService {

    private static final String DEFAULT_FILE_PATH = "users/%s.json";
    private final JSONWriterReader jsonWriterReader;

    public JSONAuthService(JSONWriterReader jsonWriterReader) {
        this.jsonWriterReader = jsonWriterReader;
    }

    @Override
    public User authenticate(String email, String pwd) throws AuthenticationException {
        String hash = buildHash(email, pwd);
        User user = readUser(buildPath(hash));
        if (user.getMail().equals(email) && user.getPassword().equals(pwd))
            return user;

        throw new AuthenticationException("Authorization failed");
    }

    private String buildPath(String hash) {
        return String.format(DEFAULT_FILE_PATH, hash);
    }

    @Override
    public User signup(User user) throws AuthenticationException {
        String hash = buildHash(user.getMail(), user.getPassword());
        String path = buildPath(hash);
        jsonWriterReader.writeToJson(path, JSONUser.toJson(user));
        return readUser(path);
    }

    private String buildHash(String mail, String password) {
        try {
            final byte[] byteKey = password.getBytes(StandardCharsets.UTF_8);
            Mac sha512Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(mail.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(macData);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    private User readUser(String path) throws AuthenticationException {
        try {
            return JSONUser.fromJson(jsonWriterReader.readFromJson(path, JSONUser.class));
        } catch (IOException e) {
            throw new AuthenticationException("Authorization failed");
        }
    }


    public static class JSONUser {

        private final String username;
        private final String mail;
        private final String password;
        private final Map<StorageService, Map<String, String>> storageServiceConfig;
        private Map<String, Map<String, String>> keyChain;

        private JSONUser(String username, String mail, String password, Map<StorageService, Map<String, String>> storageServiceConfig) {
            this.username = username;
            this.mail = mail;
            this.password = password;
            this.storageServiceConfig = storageServiceConfig;
        }

        public static JSONUser toJson(User user) {
            JSONUser jsonUser = new JSONUser(user.getUsername(), user.getMail(), user.getPassword(), user.getStorageServiceConfig());
            jsonUser.keyChain = user.getKeyChain().entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey, entry -> serializeKeyChain(entry.getValue())
                    ));
            return jsonUser;
        }

        private static Map<String, String> serializeKeyChain(Key secretKey) {
            assert secretKey != null;
            Map<String, String> mapped = new HashMap<>();
            mapped.put("algorithm", secretKey.getType());
            mapped.put("secret", new String(secretKey.getKey().getEncoded()));
            return mapped;
        }

        private static Map<String, Key> deserializeKeyChain(Map<String, Map<String, String>> keyChain) {
            assert keyChain != null;
            return keyChain.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey, entry ->
                                new Key(new SecretKeySpec(
                                        entry.getValue().get("secret").getBytes(StandardCharsets.UTF_8),
                                        entry.getValue().get("algorithm")))

                    ));
        }
        public static User fromJson(JSONUser jsonUser) {
            return new User(
                    jsonUser.username,
                    jsonUser.mail,
                    jsonUser.password,
                    jsonUser.storageServiceConfig,
                    deserializeKeyChain(jsonUser.keyChain)
            );
        }
    }

}
