package ch.psc.gui;

import ch.psc.domain.common.context.UserContext;
import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.ProcessState;
import ch.psc.domain.storage.StorageManager;
import ch.psc.domain.storage.service.FileStorage;
import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.components.fileBrowser.FileBrowserTreeTableView;
import ch.psc.gui.components.fileBrowser.FileRow;
import ch.psc.gui.util.JavaFxUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import javafx.animation.FadeTransition;
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
import javafx.util.Duration;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Controller for file browser gui. Handles drag-n-drop for uploading files.
 *
 * @author SandroGuerotto, sevimrid
 */
public class FileBrowserController extends ControlledScreen {

    @FXML
    private BorderPane fileBrowser;

    @FXML
    private FlowPane stat_pane;
    @FXML
    private FlowPane nav_pane;
    @FXML
    private FlowPane statusPane;

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
        service_tab.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            activeStorageService = storageManager.getStorageOptions().get(service_tab.getTabs().indexOf(newValue));
            activeFileBrowserTreeTableView = (FileBrowserTreeTableView) newValue.getContent();
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

    /**
     * Builds a tab for given storage service
     *
     * @param fileStorage current storage service
     * @return tab for storage service
     */
    private Tab buildServiceTab(FileStorage fileStorage) {
        Tab tab = new Tab(fileStorage.getName());
        tab.setContent(buildTreeView(fileStorage));
        return tab;
    }

    /**
     * Create a tree view from given storage service.
     *
     * @param fileStorage current storage service
     * @return file browser tree view
     */
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

    /**
     * Handles drag-n-drop over the UI.
     * Retrieves all dragged item and uploads them via {@link StorageManager}.
     *
     * @param fileStorage active storage service
     * @param event       drag event, contains dragged items
     */
    private void handleUploadDragDrop(FileStorage fileStorage, DragEvent event) {
        Dragboard db = event.getDragboard();
        if (event.getDragboard().hasFiles()) {
            db.getFiles().forEach(file -> storageManager.uploadFiles(fileStorage, file, progress -> uploadProgress(progress, activeStorageService, file)));
        }
        event.setDropCompleted(true);
        event.consume();
    }

    /**
     * Builds breadcrumb navigation.
     *
     * @param path        current path
     * @param fileStorage current storage service
     * @param tree        active file tree
     */
    private void updateNavBar(String path, FileStorage fileStorage, FileBrowserTreeTableView tree) {
        nav_pane.getChildren().clear();

        nav_pane.getChildren().add(createNavButton(fileStorage, fileStorage.getName(), fileStorage.getRoot(), tree));

        StringBuilder pathBuilder = new StringBuilder(fileStorage.getRoot() + fileStorage.getSeparator());
        splitPath(path, fileStorage.getRoot(), fileStorage.getSeparator()).forEach(part -> {
            pathBuilder.append(part);
            JFXButton navButton = createNavButton(fileStorage, part, pathBuilder.toString(), tree);
            pathBuilder.append(fileStorage.getSeparator());
            nav_pane.getChildren().addAll(new Label("/"), navButton);
        });
    }

    /**
     * Splits path at the separator into a stream.
     * First entry is skipped, because its root entry.
     *
     * @param path      current path
     * @param root      root path
     * @param separator path separator
     * @return stream path
     */
    private Stream<String> splitPath(String path, String root, String separator) {
        path = path.replace(root, "");
        System.out.println(path);
        return Arrays.stream(path.split(Pattern.quote(separator))).skip(1);
    }

    /**
     * Creates the button for bread crumb navigation.
     *
     * @param fileStorage current storage service
     * @param name        name of bread crumb
     * @param path        path to reload files
     * @param tree        current file tree
     * @return bread crumb button
     */
    private JFXButton createNavButton(FileStorage fileStorage, String name, final String path, FileBrowserTreeTableView tree) {
        JFXButton root = new JFXButton(name);
        root.setOnAction(event -> {
            storageManager.loadManagedFiles(fileStorage, path, fileTree -> buildTree(fileTree, tree.getRoot()));
            updateNavBar(path, fileStorage, tree);
        });
        return root;
    }

    /**
     * On change of path (eg user selects a folder), the new directory gets loaded via {@link StorageManager} and gets displayed.
     *
     * @param path        new path
     * @param fileStorage active file storage
     * @param tree        active tree to update content
     */
    private void updateCurrentDirectory(String path, FileStorage fileStorage, FileBrowserTreeTableView tree) {
        Platform.runLater(() -> {
            tree.setRoot(new TreeItem<>(new FileRow(new PscFile())));
            storageManager.loadManagedFiles(fileStorage, path, fileTree -> buildTree(fileTree, tree.getRoot()));
            tree.refresh();
        });
    }

    /**
     * Updates tree content and adds loaded files from storage service
     *
     * @param files loaded files
     * @param root  selected directory
     */
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
        Label label = new Label(Config.getResourceText("fileBrowser.functionNotYetImplemented"));
        FadeTransition fade = new FadeTransition(Duration.millis(2500), label);
        fade.setOnFinished(event -> statusPane.getChildren().remove(label));
        fade.play();
        statusPane.getChildren().add(label);
    }

    /**
     * Handles upload action from upload button
     */
    @FXML
    private void upload() {
        File file = new FileChooser().showOpenDialog(primaryStage);
        if (file != null) {
            storageManager.uploadFiles(activeStorageService, file, progress -> uploadProgress(progress, activeStorageService, file));
        }
    }

    /**
     * Creates a progress label to display to upload state
     *
     * @param progress             current upload state
     * @param activeStorageService storage to upload
     * @param file                 file to upload
     */
    private void uploadProgress(ProcessState progress, FileStorage activeStorageService, File file) {
        Platform.runLater(() -> {
            final Label label = getProgressLabel("u-" + file.getName());

            label.setText(progress.name() + " " + file.getName() + " ...");
            if (progress.equals(ProcessState.FINISHED)) {
                activeStorageService.getUsedStorageSpace();
                FadeTransition fade = new FadeTransition(Duration.millis(2500), label);
                fade.setOnFinished(event -> statusPane.getChildren().remove(label));
                fade.play();
            }
        });
    }

    /**
     * Handles download action from upload button
     */
    @FXML
    private void download() {
        PscFile file = activeFileBrowserTreeTableView.getSelectionModel().getSelectedItem().getValue().getFile();
        storageManager.downloadFiles(activeStorageService, file, progress -> downloadProgress(progress, file));
    }

    /**
     * Creates a progress label to display to download state
     *
     * @param progress current download state
     * @param file     file to download
     */
    private void downloadProgress(ProcessState progress, PscFile file) {
        Platform.runLater(() -> {
            final Label label = getProgressLabel("d-" + file.getName());

            label.setText(progress.name() + " " + file.getName() + " ...");
            if (progress.equals(ProcessState.FINISHED)) {
                FadeTransition fade = new FadeTransition(Duration.millis(2500), label);
                fade.setOnFinished(event -> statusPane.getChildren().remove(label));
                fade.play();
            }
        });
    }

    /**
     * Seeks and retrieves the progress level with given id.
     * if no label is found a new one gets added to {@link #statusPane}
     *
     * @param id label id
     * @return progress label
     */
    private Label getProgressLabel(String id) {
        return (Label) statusPane.getChildren()
                .stream()
                .filter(node -> node.getId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    Label label = new Label();
                    label.setId(id);
                    statusPane.getChildren().add(label);
                    return label;
                });
    }

    @Override
    public Parent getRoot() {
        return fileBrowser;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return JavaFxUtils.RegisteredScreen.FILE_BROWSER_PAGE;
    }

    /**
     * Builds storage service statistic pane.
     * Shows the amount of free and total space
     *
     * @param service storage service
     * @return statistic pane
     */
    private Region buildStatPane(FileStorage service) {
        HBox root = new HBox(10);
        Label name = new Label(service.getName());
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_RIGHT);
        BigDecimal totalStorageSpace = service.getTotalStorageSpace();
        BigDecimal usedStorageSpace = service.getUsedStorageSpace();

        ProgressBar availStorage = new ProgressBar();
        availStorage.setPrefSize(150, 20);

        availStorage.progressProperty().addListener((observable, oldValue, newValue) -> applyStatCSS(availStorage, newValue.doubleValue()));
        availStorage.setProgress(usedStorageSpace.divide(totalStorageSpace, 3, RoundingMode.HALF_EVEN).doubleValue());

        Label label = new Label(storageAmountText(totalStorageSpace.longValue(), usedStorageSpace.longValue()));

        service.getUsedStorageSpaceProperty().addListener((observable, oldValue, newValue) -> {
            availStorage.setProgress(newValue.divide(totalStorageSpace, 3, RoundingMode.HALF_EVEN).doubleValue());
            label.setText(storageAmountText(totalStorageSpace.longValue(), newValue.longValue()));
        });

        label.setPadding(new Insets(0, 5, 0, 0));
        stack.getChildren().addAll(
                availStorage,
                label
        );

        root.getChildren().addAll(name, stack);
        return root;
    }


    private String storageAmountText(long totalStorageSpace, long usedStorageSpace) {
        return String.format("%s / %s", JavaFxUtils.formatSize(usedStorageSpace), JavaFxUtils.formatSize(totalStorageSpace));
    }

    /**
     * Applies the css-class to the progress bar according to its progress (in %)
     *
     * @param progressBar progress bar to style
     * @param used        progress bar of progress
     */
    private void applyStatCSS(ProgressBar progressBar, double used) {
        progressBar.getStyleClass().removeAll(
                "progress-red",
                "progress-orange",
                "progress-green",
                "progress-yellow"
        );

        if (used < 0.7) {
            progressBar.getStyleClass().add("progress-green");
        } else if (used < 0.8) {
            progressBar.getStyleClass().add("progress-yellow");
        } else if (used < 0.9) {
            progressBar.getStyleClass().add("progress-orange");
        } else {
            progressBar.getStyleClass().add("progress-red");
        }
    }
}
