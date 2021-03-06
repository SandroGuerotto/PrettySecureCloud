package ch.psc.gui.components.fileBrowser;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import ch.psc.domain.file.EncryptionState;
import ch.psc.gui.Config;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeTableColumn;

/**
 * Creates a tree table view with columns: name, size, last changed.
 */
public class FileBrowserTreeTableView extends JFXTreeTableView<FileRow> {

  public FileBrowserTreeTableView() {
    buildColumn();
  }

  /**
   * Creates all column for the table view.
   */
  private void buildColumn() {
    JFXTreeTableColumn<FileRow, SimpleObjectProperty<FontAwesomeIconView>> iconCol =
        new JFXTreeTableColumn<>("");
    iconCol.setMaxWidth(30);
    iconCol.setMinWidth(30);
    iconCol.setCellValueFactory((
        TreeTableColumn.CellDataFeatures<FileRow, SimpleObjectProperty<FontAwesomeIconView>> param) ->
            new SimpleObjectProperty(createIcon(param.getValue().getValue())));

    JFXTreeTableColumn<FileRow, String> nameCol =
        new JFXTreeTableColumn<>(Config.getResourceText("fileBrowserColumn.name"));

    nameCol.setPrefWidth(300);
    nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileRow, String> param) ->
        param.getValue().getValue().nameProperty());

    JFXTreeTableColumn<FileRow, String> sizeCol =
        new JFXTreeTableColumn<>(Config.getResourceText("fileBrowserColumn.size"));
    sizeCol.setPrefWidth(150);
    sizeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileRow, String> param) ->
        param.getValue().getValue().sizeProperty());

    JFXTreeTableColumn<FileRow, String> lastChange =
        new JFXTreeTableColumn<>(Config.getResourceText("fileBrowserColumn.lastChangedOn"));
    lastChange.setPrefWidth(160);
    lastChange
        .setCellValueFactory((TreeTableColumn.CellDataFeatures<FileRow, String> param) ->
            param.getValue().getValue().lastChangedProperty());


    this.setShowRoot(false);
    nameCol.setSortType(TreeTableColumn.SortType.DESCENDING);
    this.getSortOrder().add(nameCol);
    this.sort();
    this.getColumns().setAll(iconCol, nameCol, sizeCol, lastChange);
    this.getStyleClass().add("file-browser-view");
  }

  private FontAwesomeIconView createIcon(FileRow file) {
    return new FontAwesomeIconView(file.isDirectoryProperty().get() ? FontAwesomeIcon.FOLDER
        : file.getFile().getEncryptionState().equals(EncryptionState.ENCRYPTED)
            ? FontAwesomeIcon.SHIELD
            : FontAwesomeIcon.FILE);
  }

}
