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
 * Implementation of a basic authentication service.
 * Each user has its own file. Each file is uniquely identifiable
 * through a combination of email and password.
 *
 * @author Sandro Guerotto
 */
public class JSONAuthenticationService implements AuthenticationService {

    private static final String DEFAULT_FILE_PATH = "users/%s.json";
    private static final String HMAC_SHA_256 = "HmacSHA256";
    private final JSONWriterReader jsonWriterReader;

    public JSONAuthenticationService(JSONWriterReader jsonWriterReader) {
        this.jsonWriterReader = jsonWriterReader;
    }

    @Override
    public User authenticate(String email, String password) throws AuthenticationException {
        String hash = buildHash(email, password);
        User user = readUser(buildPath(hash));
        if (user.getMail().equals(email) && user.getPassword().equals(password)) return user;

        throw new AuthenticationException("Authorization failed");
    }

    /**
     * Builds path to users directory.
     *
     * @param hash calculated file name
     * @return path to file
     */
    private String buildPath(String hash) {
        return String.format(DEFAULT_FILE_PATH, hash);
    }

    @Override
    public User signup(User user) throws AuthenticationException {
        String hash = buildHash(user.getMail(), user.getPassword());
        String path = buildPath(hash);
        assert jsonWriterReader.writeToJson(path, JSONUser.toJson(user));
        return readUser(path);
    }

    /**
     * Creates a hash with content using a secret key. HmacSHA256 is used to calculate hash.
     * If an error occurs during calculation of the HmacSHA256, a fallback base64-encoded value is used.
     *
     * @param content content to hash
     * @param secret  secret for hmac
     * @return calculated hash
     */
    private String buildHash(String content, String secret) {
        try {
            final byte[] byteKey = secret.getBytes(StandardCharsets.UTF_8);
            Mac sha512Hmac = Mac.getInstance(HMAC_SHA_256);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA_256);
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(content.getBytes());

            return Base64.getEncoder().encodeToString(macData);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return Base64.getEncoder().encodeToString(secret.getBytes());
        }
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

    /**
     * Reads user from users directory
     *
     * @param path path to file
     * @return depersonalized user
     * @throws AuthenticationException if the user does not exist
     */
    private User readUser(String path) throws AuthenticationException {
        try {
            return JSONUser.fromJson(jsonWriterReader.readFromJson(path, JSONUser.class));
        } catch (IOException e) {
            throw new AuthenticationException("Authorization failed");
        }
    }

    /**
     * Helper class to map external User object to serializable object.
     */
    protected static class JSONUser {

        private static final String JSON_TOKEN_SECRET = "secret";
        private static final String JSON_TOKEN_ALGORITHM = "algorithm";
        private final String username;
        private final String mail;
        private final String password;
        private final Map<StorageService, Map<String, String>> storageServiceConfig;
        private Map<String, Map<String, String>> keyChain;

        JSONUser(String username, String mail, String password, Map<StorageService, Map<String, String>> storageServiceConfig) {
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
            jsonUser.keyChain = user.getKeyChain()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> jsonUser.serializeKeyChain(entry.getValue())));
            return jsonUser;
        }

        /**
         * Create a string-map with algorithm and secret of given key.
         *
         * @param secretKey encryption key
         * @return map with algorithm and secret
         */
        private Map<String, String> serializeKeyChain(Key secretKey) {
            assert secretKey != null;
            Map<String, String> mapped = new HashMap<>();
            mapped.put(JSON_TOKEN_ALGORITHM, secretKey.getType());
            mapped.put(JSON_TOKEN_SECRET, new String(secretKey.getKey().getEncoded()));
            return mapped;
        }

        private Key mapToKey(Map<String, String> map) {
            return new Key(new SecretKeySpec(map.get(JSON_TOKEN_SECRET).getBytes(StandardCharsets.UTF_8), map.get(JSON_TOKEN_ALGORITHM)));
        }


        /**
         * Converts a string-map of algorithm and secret to a map of type and {@link Key}.
         *
         * @param keyChain string-map of algorithm and secret
         * @return map of type and {@link Key}
         */
        private Map<String, Key> deserializeKeyChain(Map<String, Map<String, String>> keyChain) {
            if (keyChain != null)
                return keyChain.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> mapToKey(entry.getValue())
                        ));
            return new HashMap<>();
        }

        /**
         * Helper method to deserialize {@link JSONUser} to a {@link User}.
         *
         * @param jsonUser serialized user
         * @return deserialize user
         */
        public static User fromJson(JSONUser jsonUser) {
            assert jsonUser != null;
            return new User(
                    jsonUser.username,
                    jsonUser.mail,
                    jsonUser.password,
                    jsonUser.storageServiceConfig,
                    jsonUser.deserializeKeyChain(jsonUser.keyChain));
        }
    }

}
