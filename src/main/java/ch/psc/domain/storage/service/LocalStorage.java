package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * Contains methods to perform local file operations
 *
 * @author sevimrid, walchr01
 */
public class LocalStorage implements FileStorage {

    private final String name;
    private final String rootPath;
    private double maxStorage;
    private String currentPath;
    private final SimpleObjectProperty<BigDecimal> usedStorageSpaceProperty = new SimpleObjectProperty<>();

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /**
     * Creates new LocalStorage and sets rootPath
     *
     * @param rootPath Path where the root is located on the system
     */
    public LocalStorage(String rootPath) {
        this.rootPath = rootPath;
        String[] parts = rootPath.split("\\\\");
        name = parts[parts.length - 1];
        currentPath = rootPath;
        setMaxStorage();
    }


    /**
     * Stores a file on the local System
     *
     * @param fileName    to upload
     * @param inputStream of the file to be saved
     */
    @Override
    public void upload(String fileName, InputStream inputStream) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(currentPath + fileName)) {
            fileOutputStream.write(inputStream.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Downloads the file from local storage
     *
     * @param path to download
     * @return InputStream of specified file
     */
    @Override
    public InputStream download(String path) {
        try {
            return new FileInputStream(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Gets the used space on the System
     * in GB.
     *
     * @return BigDezimal in GB
     */
    @Override
    public BigDecimal getUsedStorageSpace() {
        File root = new File(rootPath);
        long freeSpace = root.getFreeSpace();
        usedStorageSpaceProperty.set(new BigDecimal(freeSpace));
        return usedStorageSpaceProperty.get();
    }

    /**
     * Gets the total storage space in
     * GB.
     *
     * @return BigDezimal in GB
     */
    @Override
    public BigDecimal getTotalStorageSpace() {
        return new BigDecimal(maxStorage);
    }

    /**
     * Get files in specified path in PscFile object format.
     *
     * @param path of directory
     * @return List of PscFiles
     */
    @Override
    public List<PscFile> getFiles(String path) {
        currentPath = path;
        List<PscFile> fileList = new ArrayList<>();
        for (File child : Objects.requireNonNull(new File(path).listFiles())) {
            fileList.add(
                    new PscFile(child.getName(), child.getPath(), child.length(), new Date(child.lastModified()), child.isDirectory()));
        }

        return fileList;
    }

    /**
     * Name of local storage
     *
     * @return String name of local storage
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get usage storage Space property for gui.
     * Indication for used space on local system in
     * GB.
     *
     * @return ObjectProperty BigDecimal in GB
     */
    @Override
    public ObjectProperty<BigDecimal> getUsedStorageSpaceProperty() {
        return usedStorageSpaceProperty;
    }

    /**
     * Gets rootPath
     *
     * @return rootPath
     */
    @Override
    public String getRoot() {
        return rootPath;
    }

    /**
     * Gets separator for path fiel operations.
     *
     * @return Separator
     */
    @Override
    public String getSeparator() {
        return FILE_SEPARATOR;
    }


    /**
     * Set maximum storage for local storage.
     */
    private void setMaxStorage() {
        File[] paths = File.listRoots();
        for (File path : paths) {
            maxStorage += new File(path.toString()).getTotalSpace();
        }
    }

}
