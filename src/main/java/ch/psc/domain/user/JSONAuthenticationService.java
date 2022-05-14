package ch.psc.domain.user;

import ch.psc.datasource.JSONWriterReader;
import ch.psc.domain.cipher.Key;
import ch.psc.domain.storage.service.StorageService;
import ch.psc.exceptions.AuthenticationException;
import ch.psc.exceptions.KeyDeSerializationException;
import ch.psc.exceptions.UpdateUserException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.util.io.pem.PemObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
        return String.format(DEFAULT_FILE_PATH, hash.replace("/", ""));
    }

    @Override
    public User signup(User user) throws AuthenticationException {
        String hash = buildHash(user.getMail(), user.getPassword());
        String path = buildPath(hash);
        if (!jsonWriterReader.writeToJson(path, JSONUser.toJson(user)))
            throw new AuthenticationException("Failed to write");
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
        try {
            if (!jsonWriterReader.writeToJson(path, JSONUser.toJson(user)))
                throw new UpdateUserException("Failed to update user. Write not possible");
            return readUser(path);
        } catch (Exception e) {
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
            throw new AuthenticationException("User does not exist");
        }
    }

    /**
     * Helper class to map external User object to serializable object.
     */
    protected static class JSONUser {

        private static final Charset KEY_ENCODING = StandardCharsets.US_ASCII;
        private static final String JSON_TOKEN_SECRET = "secret";
        private static final String JSON_TOKEN_ALGORITHM = "algorithm";
        private static final String JSON_TOKEN_IS_PUBLIC = "isPublic";
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
         * @throws KeyDeSerializationException if key serialization failed
         */
        public static JSONUser toJson(User user) throws KeyDeSerializationException {
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
         * @throws KeyDeSerializationException if key serialization failed
         */
        private static Map<String, Map<String, String>> serializeKeyChain(Map<String, Key> keyChain) throws KeyDeSerializationException {
            Map<String, Map<String, String>> serialized = new HashMap<>();
            for (Entry<String, Key> entry : keyChain.entrySet()) {
                serialized.put(entry.getKey(), serializeKey(entry.getValue()));
            }

            return serialized;
        }

        /**
         * Create a string-map with algorithm and secret of given key.
         *
         * @param secretKey encryption key
         * @return map with algorithm and secret
         * @throws KeyDeSerializationException if key serialization failed
         */
        private static Map<String, String> serializeKey(Key secretKey) throws KeyDeSerializationException {
            assert secretKey != null;

            String key = new String(secretKey.getKey().getEncoded(), KEY_ENCODING);
            try {
                if (secretKey.getKey() instanceof PrivateKey) {
                    PrivateKey priv = (PrivateKey) secretKey.getKey();
                    JcaPKCS8Generator generator = new JcaPKCS8Generator(priv, null);
                    PemObject obj = generator.generate();
                    StringWriter sWriter = new StringWriter();
                    try (JcaPEMWriter pemWriter = new JcaPEMWriter(sWriter)) {
                        pemWriter.writeObject(obj);
                    }
                    key = sWriter.toString();
                } else if (secretKey.getKey() instanceof PublicKey) {
                    PublicKey pub = (PublicKey) secretKey.getKey();
                    StringWriter sWriter = new StringWriter();
                    try (JcaPEMWriter writer = new JcaPEMWriter(sWriter)) {
                        writer.writeObject(pub);
                    }
                    key = sWriter.toString();

                }
            } catch (IOException e) {
                throw new KeyDeSerializationException("Cannot serialize Key!", e);
            }

            Map<String, String> mapped = new HashMap<>();
            mapped.put(JSON_TOKEN_IS_PUBLIC, String.valueOf(secretKey.getKey() instanceof PublicKey));
            mapped.put(JSON_TOKEN_ALGORITHM, secretKey.getType());
            mapped.put(JSON_TOKEN_SECRET, key);
            return mapped;
        }

        private static Key deserializeToKey(Map<String, String> map) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
            boolean isPublic = Boolean.parseBoolean(map.get(JSON_TOKEN_IS_PUBLIC));
            String algorythm = map.get(JSON_TOKEN_ALGORITHM);
            java.security.Key secretKey = null;

            if (algorythm.contains("RSA")) {
                KeyFactory factory = KeyFactory.getInstance(map.get(JSON_TOKEN_ALGORITHM));
                if (isPublic) {
                    try (StringReader reader = new StringReader(map.get(JSON_TOKEN_SECRET))) {
                        PEMParser parser = new PEMParser(reader);
                        SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) parser.readObject();
                        byte[] encoded = publicKeyInfo.getEncoded();
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
                        secretKey = factory.generatePublic(keySpec);
                    }
                } else {
                    try (StringReader reader = new StringReader(map.get(JSON_TOKEN_SECRET))) {
                        PEMParser pemparser = new PEMParser(reader);
                        PrivateKeyInfo keyInfo = (PrivateKeyInfo) pemparser.readObject();

                        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyInfo.getEncoded());
                        secretKey = factory.generatePrivate(keySpec);

                    }
                }
            } else {
                secretKey = new SecretKeySpec(map.get(JSON_TOKEN_SECRET).getBytes(KEY_ENCODING), map.get(JSON_TOKEN_ALGORITHM));
            }

            return new Key(secretKey);
        }


        /**
         * Converts a string-map of algorithm and secret to a map of type and {@link Key}.
         *
         * @param keyChain string-map of algorithm and secret
         * @return map of type and {@link Key}
         * @throws KeyDeSerializationException If a Key cannot be deserialized, the cause is wrapped in this exception.
         */
        private static Map<String, Key> deserializeKeyChain(Map<String, Map<String, String>> keyChain) throws KeyDeSerializationException {
            Map<String, Key> deserialized = new HashMap<>();
            if (keyChain != null)
                for (Entry<String, Map<String, String>> entry : keyChain.entrySet()) {
                    try {
                        deserialized.put(entry.getKey(), deserializeToKey(entry.getValue()));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
                        throw new KeyDeSerializationException("Could not deserialize Key with name '" + entry.getKey() + "'", e);
                    }
                }
            return deserialized;
        }

        /**
         * Helper method to deserialize {@link JSONUser} to a {@link User}.
         *
         * @param jsonUser serialized user
         * @return deserialize user
         * @throws KeyDeSerializationException If a Key cannot be deserialized, it will be wrapped in this exception.
         */
        public static User fromJson(JSONUser jsonUser) throws KeyDeSerializationException {
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
