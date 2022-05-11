package ch.psc.domain.cipher;

/**
 * Contains all available cipher algorithms used by PSC.
 */
public enum CipherAlgorithms {
    PLAIN_TEXT(true, PlainTextCipher.class),
    AES(true, AesCipher.class),
    RSA(true, RsaCipher.class);

    private final boolean isSupported;
    private final Class<? extends PscCipher> cipherClass;

    CipherAlgorithms(boolean isSupported, Class<? extends PscCipher> cipherClass) {
        this.isSupported = isSupported;
        this.cipherClass = cipherClass;
    }

    /**
     * Check if a algorithm is supported by calling {@link #isSupported}.
     */
    public boolean isSupported() {
        return isSupported;
    }

    /**
     * Get the relevant Cipher class by calling {@link #getCipherClass()}
     */
    public Class<? extends PscCipher> getCipherClass() {
        return cipherClass;
    }

}
