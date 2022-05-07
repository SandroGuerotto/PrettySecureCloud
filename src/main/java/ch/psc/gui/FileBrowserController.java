package ch.psc.gui;

import ch.psc.domain.common.context.UserContext;
import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.StorageManager;
import ch.psc.domain.storage.service.FileStorage;
import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.components.fileBrowser.FileBrowserTreeTableView;
import ch.psc.gui.components.fileBrowser.FileRow;
import ch.psc.gui.util.JavaFxUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Dummy controller
 *
 * @author Sandro, sevimrid
 */
public class FileBrowserController extends ControlledScreen {

    @FXML
    private BorderPane fileBrowser;

    @FXML
    private FlowPane stat_pane;
    @FXML
    private FlowPane nav_pane;

    @FXML
    private JFXTabPane service_tab;

    private StorageManager storageManager;

    private FileStorage activeStorageService;
    private FileBrowserTreeTableView activeFileBrowserTreeTableView;
    private final StringProperty currentPath = new SimpleStringProperty("root");


    public FileBrowserController(Stage primaryStage, Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens) {
        super(primaryStage, screens);
    }

    @Override
    protected boolean init(JavaFxUtils.RegisteredScreen previousScreen, Object... params) {
        storageManager = new StorageManager(UserContext.getAuthorizedUser());
        initialize();
        return super.init(previousScreen, params);
    }

    private void initialize() {
        primaryStage.setMinHeight(720);
        primaryStage.setMinWidth(1080);
        stat_pane.getChildren().clear();
        service_tab.getTabs().clear();
        service_tab.requestFocus();

        storageManager.loadStorageServices();
        service_tab.selectionModelProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            activeStorageService = storageManager.getStorageOptions().get(newValue.getSelectedIndex());
            activeFileBrowserTreeTableView = (FileBrowserTreeTableView) newValue.getSelectedItem().getContent();
            currentPath.set(activeStorageService.getRoot());
        });

        currentPath.addListener((observable, oldValue, newValue) -> {
            updateNavBar(newValue, activeStorageService, activeFileBrowserTreeTableView);
            updateCurrentDirectory(newValue, activeStorageService, activeFileBrowserTreeTableView);
        });
        activeStorageService = storageManager.getStorageOptions().get(0);
        storageManager.getStorageOptions().forEach(
                fileStorage -> {
                    stat_pane.getChildren().add(buildStatPane(fileStorage));
                    service_tab.getTabs().add(buildServiceTab(fileStorage));
                }
        );
        activeFileBrowserTreeTableView = (FileBrowserTreeTableView) service_tab.selectionModelProperty().get().getSelectedItem().getContent();
        currentPath.set(activeStorageService.getRoot());

    }

    private Tab buildServiceTab(FileStorage fileStorage) {
        Tab tab = new Tab(fileStorage.getName());
        tab.setContent(buildTreeView(fileStorage));

        return tab;
    }

    private FileBrowserTreeTableView buildTreeView(FileStorage fileStorage) {
        FileBrowserTreeTableView tree = new FileBrowserTreeTableView();

        TreeItem<FileRow> root = new TreeItem<>(new FileRow(new PscFile()));
        tree.setRoot(root);

        storageManager.loadManagedFiles(fileStorage, fileStorage.getRoot(), fileTree -> buildTree(fileTree, root));

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (newValue.getValue().getFile().isDirectory())
                currentPath.set(newValue.getValue().getFile().getPath());

        });

        tree.setOnDragOver(event -> event.acceptTransferModes(TransferMode.COPY));
        tree.setOnDragDropped(event -> handleUploadDragDrop(fileStorage, event));

        return tree;
    }

    private void handleUploadDragDrop(FileStorage fileStorage, DragEvent event) {
        Dragboard db = event.getDragboard();
        if (event.getDragboard().hasFiles()) {
            db.getFiles().forEach(file -> storageManager.uploadFiles(fileStorage, file));
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private void updateNavBar(String path, FileStorage fileStorage, FileBrowserTreeTableView tree) {
        nav_pane.getChildren().clear();

        nav_pane.getChildren().add(createNavButton(fileStorage, fileStorage.getName(), fileStorage.getRoot(), tree));

        StringBuilder pathBuilder = new StringBuilder("/");
        Arrays.stream(path.split("/")).skip(1).forEach(part -> {
            pathBuilder.append(part);
            JFXButton navButton = createNavButton(fileStorage, part, pathBuilder.toString(), tree);
            pathBuilder.append("/");
            nav_pane.getChildren().addAll(new Label("/"), navButton);
        });
    }

    private JFXButton createNavButton(FileStorage fileStorage, String name, final String path, FileBrowserTreeTableView tree) {
        JFXButton root = new JFXButton(name);
        root.setOnAction(event -> {
            storageManager.loadManagedFiles(fileStorage, path, fileTree -> buildTree(fileTree, tree.getRoot()));
            updateNavBar(path, fileStorage, tree);
        });
        return root;
    }

    private void updateCurrentDirectory(String path, FileStorage fileStorage, FileBrowserTreeTableView tree) {
        Platform.runLater(() -> {
            tree.setRoot(new TreeItem<>(new FileRow(new PscFile())));
            storageManager.loadManagedFiles(fileStorage, path, fileTree -> buildTree(fileTree, tree.getRoot()));
            tree.refresh();
        });
    }

    private void buildTree(List<PscFile> files, TreeItem<FileRow> root) {
        Platform.runLater(() -> {
            root.getChildren().clear();
            files.forEach(file ->
                    root.getChildren().add(
                            new TreeItem<>(new FileRow(file))
                    ));
        });

    }

    @FXML
    public void logout() {
        UserContext.setAuthorizedUser(null);
        storageManager = null;
        try {
            switchScreen(JavaFxUtils.RegisteredScreen.LOGIN_PAGE);
        } catch (ScreenSwitchException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openSettings() {

    }

    @FXML
    private void upload() {
        File file = new FileChooser().showOpenDialog(primaryStage);
        if (file != null) {
            storageManager.uploadFiles(activeStorageService, file);
        }
    }

    @FXML
    private void download() {
        PscFile file = activeFileBrowserTreeTableView.getSelectionModel().getSelectedItem().getValue().getFile();
        storageManager.downloadFiles(activeStorageService, file);
    }

    @Override
    public Parent getRoot() {
        return fileBrowser;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return JavaFxUtils.RegisteredScreen.FILE_BROWSER_PAGE;
    }


    private Region buildStatPane(FileStorage service) {
        HBox root = new HBox(10);
        Label name = new Label(service.getName());
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_RIGHT);
        double totalStorageSpace = service.getTotalStorageSpace();
        double usedStorageSpace = totalStorageSpace - service.getAvailableStorageSpace();

        ProgressBar availStorage = new ProgressBar(usedStorageSpace / totalStorageSpace);

        applyStatCSS(availStorage, availStorage.getProgress());
        availStorage.setPrefSize(150, 20);

        Label label = new Label(String.format("%.2f/%.2f GB", usedStorageSpace, totalStorageSpace));// TODO mit properties arbeiten

        service.getUsedStorageSpaceProperty().addListener((observable, oldValue, newValue) -> {
            double used = totalStorageSpace - newValue.doubleValue();
            applyStatCSS(availStorage, used);
            availStorage.setProgress(used);
            label.setText(String.format("%.2f/%.2f GB", newValue.doubleValue(), totalStorageSpace));
        });

        label.setPadding(new Insets(0, 5, 0, 0));
        stack.getChildren().addAll(
                availStorage,
                label
        );

        root.getChildren().addAll(name, stack);
        return root;
    }

    private void applyStatCSS(ProgressBar availStorage, double used) {
        availStorage.getStyleClass().removeAll(
                "progress-red",
                "progress-orange",
                "progress-green",
                "progress-yellow"
        );

        if (used < 0.7) {
            availStorage.getStyleClass().add("progress-green");
        } else if (used < 0.8) {
            availStorage.getStyleClass().add("progress-yellow");
        } else if (used < 0.9) {
            availStorage.getStyleClass().add("progress-orange");
        } else {
            availStorage.getStyleClass().add("progress-red");
        }
    }
}
