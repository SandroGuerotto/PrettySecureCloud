package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.ObjectProperty;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

public interface FileStorage {

    /**
     * Uploads a file to the service
     *
     * @param fileName    to upload
     * @param inputStream of the file to be saved
     */
    void upload(String fileName, InputStream inputStream);

    /**
     * Downloads a selected file from the service
     *
     * @param path to download
     * @return InputStream of the file to be downloaded
     */
    InputStream download(String path);

    /**
     * Calculates the used memory in bytes
     *
     * @return Amount of used space in the system in bytes
     */
    BigDecimal getUsedStorageSpace();

    BigDecimal getTotalStorageSpace();

    /**
     * Creates a list with all files/folders in directory, adds filesize, lastModified and isDirectory
     * to PscFile attributes.
     *
     * @param path of directory
     * @return List with all files/folders in directory
     */
    List<PscFile> getFiles(String path);

    /**
     * Returns the name of the connected storage service.
     *
     * @return name of storage service
     */
    String getName();

    /**
     * Returns used space of the connected storage service.
     *
     * @return used space property
     */
    ObjectProperty<BigDecimal> getUsedStorageSpaceProperty();

    /**
     * Return root path.
     *
     * @return root path
     */
    String getRoot();

    /**
     * Return the path separator of the connected storage service.
     * In Dropbox it's "/".
     *
     * @return path separator
     */
    String getSeparator();

}
