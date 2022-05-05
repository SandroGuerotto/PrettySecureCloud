package ch.psc.domain.file;

import ch.psc.domain.cipher.EncryptionState;

import java.util.Date;

public class PscFile {

    private final String name;
    private final String path;
    private byte[] data;
    private final boolean isDirectory;
    private EncryptionState encryptionState;
    private final long size;
    private final Date lastModified;

    public PscFile(String name, String path) {
        this(name, path, 0, null, false);
    }

    public PscFile(String name, String path, long size, Date lastModified, boolean isDirectory) {
        this.path = path;
        this.name = name;
        this.isDirectory = isDirectory;
        this.size = size;
        this.lastModified = lastModified;
    }

    public PscFile() {
        this("", "", 0, null, false);
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
}
