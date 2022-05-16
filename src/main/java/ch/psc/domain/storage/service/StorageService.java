package ch.psc.domain.storage.service;

/**
 * Contains all available storage types. Check if a service is supported by calling
 * {@link #isSupported}.
 */
public enum StorageService {
  DROPBOX(true,"images/dropbox/icon.png"),
  LOCAL(true,"images/local/icon.png"),
  GOOGLE_DRIVE(false,"path");

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
