package ch.psc.domain.storage.service;

import ch.psc.domain.file.PscFile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


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

    public LocalStorage(String rootPath) {
        this.rootPath = rootPath;
        String[] parts = rootPath.split("\\\\");
        name = parts[parts.length - 1];
        currentPath = rootPath;
        setMaxStorage();
    }

    @Override
    public boolean upload(PscFile file, InputStream inputStream) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(currentPath + file.getName())) {
            fileOutputStream.write(inputStream.readAllBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method will download a given file
     *
     * @param file to download
     * @return InputStream of the file to be downloaded
     */
    @Override
    public InputStream download(PscFile file) {
        try (FileInputStream fileInputStream = new FileInputStream(file.getPath())) {
            return fileInputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a double value with the free memory in bytes
     *
     * @return Amount of free space in the system in bytes
     */
    @Override
    public BigDecimal getUsedStorageSpace() {
        File root = new File(rootPath);
        long freeSpace = root.getFreeSpace();
        usedStorageSpaceProperty.set(new BigDecimal(freeSpace));
        return usedStorageSpaceProperty.get();
    }

    @Override
    public BigDecimal getTotalStorageSpace() {
        return new BigDecimal(maxStorage);
    }

    @Override
    public List<PscFile> getFiles(String path) {
        currentPath = path;

        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObjectProperty<BigDecimal> getUsedStorageSpaceProperty() {
        return usedStorageSpaceProperty;
    }

    @Override
    public String getRoot() {
        return rootPath;
    }


    private void setMaxStorage() {
        File[] paths = File.listRoots();
        for (File path : paths) {
            maxStorage += new File(path.toString()).getTotalSpace();
        }
    }

}
