package ch.psc.domain.storage.service;

public enum StorageService {
    DROPBOX(true,"images/dropbox/icon.png"), LOCAL(false,"path"), GOOGLE_DRIVE(false,"path");

    private final boolean isSupported;
    private final String imagePath;

    StorageService(boolean isSupported, String imagePath) {

        this.isSupported = isSupported;
        this.imagePath = imagePath;
    }

    public boolean isSupported() {
        return isSupported;
    }

    public String getImagePath() {
        return imagePath;
    }
}
