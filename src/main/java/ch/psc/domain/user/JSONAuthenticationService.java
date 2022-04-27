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
public class JSONAuthenticationService implements AuthenticationService {

    private static final String DEFAULT_FILE_PATH = "users/%s.json";
    private static final String HMAC_SHA_256 = "HmacSHA256";
    private final JSONWriterReader jsonWriterReader;

    public JSONAuthenticationService(JSONWriterReader jsonWriterReader) {
        this.jsonWriterReader = jsonWriterReader;
    }

    @Override
    public User authenticate(String email, String pwd) throws AuthenticationException {
        String hash = buildHash(email, pwd);
        User user = readUser(buildPath(hash));
        if (user.getMail().equals(email) && user.getPassword().equals(pwd)) return user;

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
            Mac sha512Hmac = Mac.getInstance(HMAC_SHA_256);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA_256);
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(mail.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(macData);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User update(User user) throws Exception {
        String hash = buildHash(user.getMail(), user.getPassword());
        String path = buildPath(hash);
        if (jsonWriterReader.writeToJson(path, JSONUser.toJson(user))) throw new Exception("Failed to update user");
        try {
            return readUser(path);
        } catch (AuthenticationException e) {
            throw new Exception("Failed to update user", e);
        }
    }

    private User readUser(String path) throws AuthenticationException {
        try {
            return JSONUser.fromJson(jsonWriterReader.readFromJson(path, JSONUser.class));
        } catch (IOException e) {
            throw new AuthenticationException("Authorization failed");
        }
    }


    private static class JSONUser {

        public static final String JSON_TOKEN_SECRET = "secret";
        public static final String JSON_TOKEN_ALGORITHM = "algorithm";
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

        /**
         * Helper method to serialize {@link User} to a JSON-format.
         * Used to custom serialize data of user.
         *
         * @param user user object
         * @return serializable user object
         */
        public static JSONUser toJson(User user) {
            JSONUser jsonUser = new JSONUser(user.getUsername(), user.getMail(), user.getPassword(), user.getStorageServiceConfig());
            jsonUser.keyChain = user.getKeyChain().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> serializeKeyChain(entry.getValue())));
            return jsonUser;
        }

        /**
         * Create a string-map with algorithm and secret of given key.
         * @param secretKey encryption key
         * @return map with algorithm and secret
         */
        private static Map<String, String> serializeKeyChain(Key secretKey) {
            assert secretKey != null;
            Map<String, String> mapped = new HashMap<>();
            mapped.put(JSON_TOKEN_ALGORITHM, secretKey.getType());
            mapped.put(JSON_TOKEN_SECRET, new String(secretKey.getKey().getEncoded()));
            return mapped;
        }

        /**
         * Converts a string-map of algorithm and secret to a map of type and {@link Key}.
         * @param keyChain string-map of algorithm and secret
         * @return map of type and {@link Key}
         */
        private static Map<String, Key> deserializeKeyChain(Map<String, Map<String, String>> keyChain) {
            assert keyChain != null;
            return keyChain.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new Key(new SecretKeySpec(entry.getValue().get(JSON_TOKEN_SECRET).getBytes(StandardCharsets.UTF_8), entry.getValue().get(JSON_TOKEN_ALGORITHM)))

            ));
        }

        /**
         * Helper method to deserialize {@link JSONUser} to a {@link User}.
         * @param jsonUser serialized user
         * @return deserialize user
         */
        public static User fromJson(JSONUser jsonUser) {
            return new User(jsonUser.username, jsonUser.mail, jsonUser.password, jsonUser.storageServiceConfig, deserializeKeyChain(jsonUser.keyChain));
        }
    }

}
