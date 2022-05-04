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
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        storageManager.getStorageOptions().forEach(
                fileStorage -> {
                    stat_pane.getChildren().add(buildStatPane(fileStorage));
                    service_tab.getTabs().add(buildServiceTab(fileStorage));
                }
        );
        activeStorageService = storageManager.getStorageOptions().get(0);

    }

    private Tab buildServiceTab(FileStorage fileStorage) {
        Tab tab = new Tab(fileStorage.getName());
        tab.setContent(buildTreeView(fileStorage));

        return tab;
    }

    private FileBrowserTreeTableView buildTreeView(FileStorage fileStorage) {

        FileBrowserTreeTableView tree = new FileBrowserTreeTableView();
        tree.showRootProperty().set(false);
        tree.setRoot(new TreeItem<>(new FileRow(new PscFile())));
        nav_pane.getChildren().clear();

        JFXButton root = new JFXButton(fileStorage.getName());
        root.setOnAction(event -> {
            storageManager.loadManagedFiles(fileStorage, fileStorage.getRoot(), fileTree -> buildTree(fileTree, tree.getRoot()));
            nav_pane.getChildren().clear();
            nav_pane.getChildren().add(root);
        });
        nav_pane.getChildren().add(root);

        storageManager.loadManagedFiles(fileStorage, "", fileTree -> buildTree(fileTree, tree.getRoot()));

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateCurrentDirectory(newValue, fileStorage, tree);
        });

        tree.setOnDragOver(event -> event.acceptTransferModes(TransferMode.COPY));
        tree.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (event.getDragboard().hasFiles()) {
                storageManager.uploadFiles(fileStorage,
                        db.getFiles().stream()
                                .map(file -> new PscFile(file.getPath(), file.getName(), false))
                                .collect(Collectors.toList())
                );
            }
            event.setDropCompleted(true);
            event.consume();
        });

        return tree;
    }

    private void updateCurrentDirectory(TreeItem<FileRow> selectedDirectory, FileStorage fileStorage, FileBrowserTreeTableView tree) {
        Platform.runLater(() -> {
            if (selectedDirectory == null || !selectedDirectory.getValue().getFile().isDirectory()) return;
            PscFile folder = selectedDirectory.getValue().getFile();
            System.out.println("path:" + folder.getPath());
            JFXButton nav = new JFXButton(folder.getName());
            nav.setOnAction(event ->
                    storageManager.loadManagedFiles(fileStorage, folder.getPath(), fileTree -> buildTree(fileTree, selectedDirectory)));

            nav_pane.getChildren().addAll(new Label("/"), nav);
            storageManager.loadManagedFiles(fileStorage, folder.getPath(), fileTree -> buildTree(fileTree, selectedDirectory));
            tree.setRoot(selectedDirectory);
            tree.getSelectionModel().clearSelection();
            tree.refresh();
        });
    }

    private void buildTree(List<PscFile> files, TreeItem<FileRow> tree) {
        Platform.runLater(() -> {
            tree.getChildren().clear();
            files.forEach(file ->
                    tree.getChildren().add(
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
        // filesToLoad do Encryption
        //ToDO upload
    }

    @FXML
    private void download() {
//        storageManager.downloadFiles(activeStorageService);
        // filesToLoad do Encryption
        //ToDO upload
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
