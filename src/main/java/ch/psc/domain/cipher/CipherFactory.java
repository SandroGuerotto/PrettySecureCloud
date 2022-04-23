package ch.psc.domain.cipher;

import java.util.HashMap;
import java.util.Map;

public class CipherFactory {

    private static Map<String, PscCipher> cipherMap = new HashMap();

    // add new ciphers to static block
    static {
        PlainTextCipher plainTextCipher = new PlainTextCipher();
        cipherMap.put(plainTextCipher.getAlgorithm(), plainTextCipher);
    }

    public static PscCipher createCipher(String type) {
        return cipherMap.get(type);
    }

}
