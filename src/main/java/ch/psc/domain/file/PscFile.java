package ch.psc.domain.file;

import java.util.Date;

public class PscFile {

    public static final String PSC_FILE_EXTENSION = ".psc";
  
    private final String name;
    private final String path;
    private byte[] data;
    private final boolean isDirectory;
    private EncryptionState encryptionState;
    private final long size;
    private final Date lastModified;
    private byte[] nonce;

    public PscFile(String name, String path) {
        this(name, path, EncryptionState.DECRYPTED, 0, null, false);
    }

    public PscFile(String name, String path, EncryptionState encryptionState, long size, Date lastModified, boolean isDirectory) {
        this.path = path;
        this.name = name;
        this.isDirectory = isDirectory;
        this.encryptionState = encryptionState;
        this.size = size;
        this.lastModified = lastModified;
    }

    public PscFile() {
        this("", "", EncryptionState.DECRYPTED, 0, null, false);
    }

    public long getFileSize() {
        return size;
    }
    
    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public byte[] getData() {
        return data;
    }

    public EncryptionState getEncryptionState() {
        return encryptionState;
    }

    public void setEncryptionState(EncryptionState encryptionState) {
        this.encryptionState = encryptionState;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public void setNonce(byte[] nonce) {
        this.nonce = nonce;
    }
}
