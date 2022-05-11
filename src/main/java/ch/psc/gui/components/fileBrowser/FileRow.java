package ch.psc.gui.components.fileBrowser;

import ch.psc.domain.file.PscFile;
import ch.psc.gui.util.JavaFxUtils;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FileRow extends RecursiveTreeObject<FileRow> {

    private static final String LAST_MODIFIED_DATE_PATTERN = "yyyy-MM-dd hh:mm:ss";
    private final DateFormat dateFormat = new SimpleDateFormat(LAST_MODIFIED_DATE_PATTERN);
    private final PscFile file;
    private final StringProperty name;
    private final StringProperty size;
    private final StringProperty lastChanged;
    private final BooleanProperty isDirectory;

    public FileRow(PscFile file) {
        this.file = file;
        this.name = new SimpleStringProperty(file.getName());
        lastChanged = new SimpleStringProperty(formatDate(file));
        size = new SimpleStringProperty(JavaFxUtils.formatSize(file.getFileSize()));
        isDirectory = new SimpleBooleanProperty(file.isDirectory());
    }
    

    private String formatDate(PscFile file) {
        if (file.getLastModified() == null) return "";
        return dateFormat.format(file.getLastModified());
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