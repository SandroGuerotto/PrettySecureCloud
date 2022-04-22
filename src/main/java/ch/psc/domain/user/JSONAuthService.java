package ch.psc.domain.user;

import ch.psc.datasource.JSONWriterReader;
import ch.psc.exceptions.AuthenticationException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
        assert user != null;
        if (user.getMail().equals(email) && user.getPassword().equals(pwd))
            return user;

        throw new AuthenticationException("Authorization failed");
    }

    private String buildPath(String hash) {
        return String.format(DEFAULT_FILE_PATH, hash);
    }

    @Override
    public User signup(User user) throws AuthenticationException{
        String hash = buildHash(user.getMail(), user.getPassword());
        String path = buildPath(hash);
        jsonWriterReader.writeToJson(path, user);

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
            return jsonWriterReader.readFromJson(path, User.class);
        } catch (IOException e) {
            throw new AuthenticationException("Authorization failed");
        }
    }
}
