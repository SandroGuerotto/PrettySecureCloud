package ch.psc.gui.components.fileBrowser;

import ch.psc.domain.file.PscFile;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileRow extends RecursiveTreeObject<FileRow> {

    private final PscFile file;
    private final StringProperty name;
    private final StringProperty size;
    private final StringProperty lastChanged;
    private final BooleanProperty isDirectory;

    public FileRow(PscFile file) {
        this.file = file;
        this.name = new SimpleStringProperty(file.getName());
        lastChanged = new SimpleStringProperty("18:00:10 04.05.2022");
//        size = new SimpleStringProperty(file.getFileSize() + "");
        size = new SimpleStringProperty("10 Gb");
        isDirectory = new SimpleBooleanProperty(file.isDirectory());
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public StringProperty lastChangedProperty() {
        return lastChanged;
    }

    public PscFile getFile() {
        return file;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public BooleanProperty isDirectoryProperty() {
        return isDirectory;
    }
}