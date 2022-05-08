package ch.psc.domain.user;

import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.exceptions.AuthenticationException;
import ch.psc.exceptions.UpdateUserException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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
        return String.format(DEFAULT_FILE_PATH, hash.replace("/",""));
    }

    @Override
    public User signup(User user) throws AuthenticationException {
        String hash = buildHash(user.getMail(), user.getPassword());
        String path = buildPath(hash);
        if (!jsonWriterReader.writeToJson(path, JSONUser.toJson(user)))
            throw new  AuthenticationException("Failed to write");
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
    public User update(User user) throws UpdateUserException {
        String hash = buildHash(user.getMail(), user.getPassword());
        String path = buildPath(hash);
        if (!jsonWriterReader.writeToJson(path, JSONUser.toJson(user)))
            throw new UpdateUserException("Failed to update user. Write not possible");
        try {
            return readUser(path);
        } catch (AuthenticationException e) {
            throw new UpdateUserException("Failed to update user. Read failed", e);
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
        private final Map<String, Map<String, String>> keyChain;
        private final String downloadPath;

        JSONUser(String username, String mail, String password, Map<StorageService, Map<String, String>> storageServiceConfig, Map<String, Map<String, String>> keyChain, String downloadPath) {
            this.username = username;
            this.mail = mail;
            this.password = password;
            this.storageServiceConfig = storageServiceConfig;
            this.keyChain = keyChain;
            this.downloadPath = downloadPath;
        }

        /**
         * Helper method to serialize {@link User} to a JSON-format.
         * Used to custom serialize data of user.
         *
         * @param user user object
         * @return serializable user object
         */
        public static JSONUser toJson(User user) {
            return new JSONUser(
                    user.getUsername(),
                    user.getMail(),
                    user.getPassword(),
                    user.getStorageServiceConfig(),
                    serializeKeyChain(user.getKeyChain()),
                    user.getDownloadPath());
        }

        /**
         * Create a Map of type of key and a string-map with algorithm and secret.
         * Uses {@link #serializeKey(Key)} to create the string-map.
         *
         * @param keyChain keychain of user
         * @return serialized keychain
         */
        private static Map<String, Map<String, String>> serializeKeyChain(Map<String, Key> keyChain) {
            return keyChain
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> serializeKey(entry.getValue())));
        }

        /**
         * Create a string-map with algorithm and secret of given key.
         *
         * @param secretKey encryption key
         * @return map with algorithm and secret
         */
        private static Map<String, String> serializeKey(Key secretKey) {
            assert secretKey != null;
            Map<String, String> mapped = new HashMap<>();
            mapped.put(JSON_TOKEN_ALGORITHM, secretKey.getType());
            mapped.put(JSON_TOKEN_SECRET, new String(secretKey.getKey().getEncoded()));
            System.out.print("serialized key = " + secretKey.getKey().getEncoded());
            System.out.print("serialized key = " + Arrays.toString(secretKey.getKey().getEncoded()));
            return mapped;
        }

        private static Key deserializeToKey(Map<String, String> map) {
            System.out.print("deserialized key = " + map.get(JSON_TOKEN_SECRET).getBytes(StandardCharsets.UTF_8));
            System.out.print("deserialized key = " + Arrays.toString(map.get(JSON_TOKEN_SECRET).getBytes(StandardCharsets.UTF_8)));
            return new Key(new SecretKeySpec(map.get(JSON_TOKEN_SECRET).getBytes(StandardCharsets.UTF_8), map.get(JSON_TOKEN_ALGORITHM)));
        }


        /**
         * Converts a string-map of algorithm and secret to a map of type and {@link Key}.
         *
         * @param keyChain string-map of algorithm and secret
         * @return map of type and {@link Key}
         */
        private static Map<String, Key> deserializeKeyChain(Map<String, Map<String, String>> keyChain) {
            if (keyChain != null)
                return keyChain.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> deserializeToKey(entry.getValue())
                        ));
            return Collections.emptyMap();
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
                    deserializeKeyChain(jsonUser.keyChain),
                    jsonUser.downloadPath);
        }
    }

}
