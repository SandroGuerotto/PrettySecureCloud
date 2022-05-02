package ch.psc.domain.cipher;

/**
 * Contains all available cipher algorithms.
 * Check if a algorithm is supported by calling {@link #isSupported}.
 */
public enum CipherAlgorithms {
    PLAIN_TEXT(true, PlainTextCipher.class),
    AES(false, null),
    RSA(false, null);

    private final boolean isSupported;
    private final Class<? extends PscCipher> cipherClass;

    CipherAlgorithms(boolean isSupported, Class<? extends PscCipher> cipherClass) {
        this.isSupported = isSupported;
        this.cipherClass = cipherClass;
    }

    public boolean isSupported() {
        return isSupported;
    }

    public Class<? extends PscCipher> getCipherClass(){
        return cipherClass;
    }

}
