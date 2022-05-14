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

    public LocalStorage(String rootPath) {
        this.rootPath = rootPath;
        String[] parts = rootPath.split("\\\\");
        name = parts[parts.length - 1];
        currentPath = rootPath;
        setMaxStorage();
    }


    @Override
    public void upload(String fileName, InputStream inputStream) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(currentPath + fileName)) {
            fileOutputStream.write(inputStream.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public InputStream download(String path) {
        try {
            return new FileInputStream(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


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
        List<PscFile> fileList = new ArrayList<>();
        for (File child : Objects.requireNonNull(new File(path).listFiles())) {
            fileList.add(
                    new PscFile(child.getName(), child.getPath(), child.length(), new Date(child.lastModified()), child.isDirectory()));
        }

        return fileList;
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

    @Override
    public String getSeparator() {
        return FILE_SEPARATOR;
    }


    private void setMaxStorage() {
        File[] paths = File.listRoots();
        for (File path : paths) {
            maxStorage += new File(path.toString()).getTotalSpace();
        }
    }

}
