package ch.psc.domain.storage;

/**
 * States of a download or upload process.
 * Used to notify gui about the current state of process.
 *
 * @author SandroGuerotto
 */
public enum ProcessState {
    DOWNLOADING, DOWNLOADED, ENCRYPTING, ENCRYPTED, FINISHED,
    UPLOADING, UPLOADED, DECRYPTING, DECRYPTED
}
