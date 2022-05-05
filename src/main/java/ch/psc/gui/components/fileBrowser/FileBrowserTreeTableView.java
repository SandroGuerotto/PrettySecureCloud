package ch.psc.gui.components.fileBrowser;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeTableColumn;

public class FileBrowserTreeTableView extends JFXTreeTableView<FileRow> {

    public FileBrowserTreeTableView() {
        buildColumn();
    }

    private void buildColumn() {
        JFXTreeTableColumn<FileRow, SimpleObjectProperty<FontAwesomeIconView>> iconCol = new JFXTreeTableColumn<>("");
        iconCol.setMaxWidth(30);
        iconCol.setMinWidth(30);
        iconCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileRow, SimpleObjectProperty<FontAwesomeIconView>> param) ->
                new SimpleObjectProperty(
                        new FontAwesomeIconView(param.getValue().getValue().isDirectoryProperty().get() ? FontAwesomeIcon.FOLDER : FontAwesomeIcon.FILE))
        );

        JFXTreeTableColumn<FileRow, String> nameCol = new JFXTreeTableColumn<>("Name");

        nameCol.setPrefWidth(300);
        nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileRow, String> param) -> param.getValue().getValue().nameProperty());

        JFXTreeTableColumn<FileRow, String> sizeCol = new JFXTreeTableColumn<>("Size");
        sizeCol.setPrefWidth(150);
        sizeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileRow, String> param) -> param.getValue().getValue().sizeProperty());

        JFXTreeTableColumn<FileRow, String> lastChange = new JFXTreeTableColumn<>("last changed on");
        lastChange.setPrefWidth(150);
        lastChange.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileRow, String> param) -> param.getValue().getValue().lastChangedProperty());


        this.setShowRoot(false);
        nameCol.setSortType(TreeTableColumn.SortType.DESCENDING);
        this.getSortOrder().add(nameCol);
        this.sort();
        this.getColumns().setAll(iconCol, nameCol, sizeCol, lastChange);
        this.getStyleClass().add("file-browser-view");
    }

}
