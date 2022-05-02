package ch.psc.gui;

import ch.psc.datasource.datastructure.Tree;
import ch.psc.domain.file.PscFile;
import ch.psc.domain.storage.service.LocalStorage;
import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.util.JavaFxUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.input.TransferMode;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ch.psc.gui.components.fileBrowser.FilePathTreeItem;

/**
 * Controller for the LoginView.
 * Contains everything the controller has to reach in the login view and all methods the login view calls based on events.
 *
 * @author waldbsaf
 * @version 1.0
 */

public class FileBrowserController extends ControlledScreen{

    @FXML
    private BorderPane fileBrowser;
    @FXML
    private TreeView dragAndDropArea;
    @FXML
    private TreeView treeView;
    @FXML
    private ProgressBar localStorageSpace;
    @FXML
    private ProgressBar encryption;
    @FXML
    private ProgressBar upload;
    @FXML
    private Label availableLocalSpaceText;

    private LocalStorage localStorage;
    private Tree<PscFile> tree;
    private TreeItem<String> rootNode;


    public FileBrowserController(Stage primaryStage, Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens) {
        super(primaryStage, screens);
        localStorage = new LocalStorage();
    }
    EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
        handleMouseClicked(event);
    };

    private void handleMouseClicked(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        node = node.getParent();
        StringBuilder pahtBuilder = new StringBuilder();
        System.out.println(node.getParent());
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
//        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
//            String name = (String) ((TreeItem)treeView.getSelectionModel().getSelectedItem()).getValue();
//            System.out.println("Node click: " + name);
//        }
    }
    public void initialize(){
        MultipleSelectionModel<TreeItem<String>> tvSelModel = treeView.getSelectionModel();
        // Use a change listener to respond to a selection within
        // a tree view
        tvSelModel.selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
                    public void changed(ObservableValue<? extends TreeItem<String>> changed,
                            TreeItem<String> oldVal,
                            TreeItem<String> newVal) {
                        // Display the selection and its complete path from the root.
                        if (newVal != null) {

                            // Construct the entire path to the selected item.
                            String path = newVal.getValue();
                            TreeItem<String> tmp = newVal.getParent();
                            while (tmp != null) {
                                path = tmp.getValue() + "/" + path;
                                tmp = tmp.getParent();
                            }
                            String hostName = "computer";
                            try {
                                hostName = InetAddress.getLocalHost().getHostName();
                            } catch (UnknownHostException x) {
                            }
                            path = path.replace(hostName,"C:");
                            // Display the selection and the entire path.
                            System.out.println("Selection is " +
                                    newVal.getValue() + "\nComplete path is " + path);
                            TreeItem c = tvSelModel.getSelectedItem();
                            getSubtree(c,new File(path));


                        }
                    }
                });

        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
            handleMouseClicked(event);
        };

        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);

        Node dragAndDropAreaNode = dragAndDropArea;
        dragAndDropAreaNode.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
        });

        dragAndDropAreaNode.setOnDragDropped(event -> {
           Dragboard db = event.getDragboard();
           if(event.getDragboard().hasFiles()){
               List<File> filesToLoad = db.getFiles();
               TreeItem<String> rootNode = new TreeItem<>("Computer");
               dragAndDropArea.setShowRoot(false);
               for(File fileToLoad: filesToLoad){
                   FilePathTreeItem treeNode = new FilePathTreeItem(fileToLoad.toPath());
                   rootNode.getChildren().add(treeNode);
               }
               dragAndDropArea.setRoot(rootNode);
            }
        });
        File[] paths = File.listRoots();
        String path = paths[0].toString();
        //displayTreeView("C:\\Something");

        availableLocalSpaceText.setText(String.format("%.2f GB",localStorage.getAvailableStorageSpace()));
        localStorageSpace.setProgress(getPercentageOfAvailableStorageSpace());


        String hostName = "computer";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException x) {
        }
        rootNode = new TreeItem<>(hostName,new ImageView(new Image(ClassLoader.getSystemResourceAsStream("images/fileBrowser/computer.png"))));
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
        File fileInputDirectoryLocation = new File("C:\\");
        File[] fileList = fileInputDirectoryLocation.listFiles();
        for(File file : fileList){
            Path name = file.toPath();
            FilePathTreeItem treeNode = new FilePathTreeItem(name);
            rootNode.getChildren().add(treeNode);

        }

        //getSubtree(rootNode,fileInputDirectoryLocation);


        rootNode.setExpanded(true);
        treeView.setRoot(rootNode);
    }

    private void getSubtree(TreeItem parentNode, File parent){
        File[] parentContent = parent.listFiles();
        if(parentContent != null) {
            if (parentContent.length != 0) {
                for (File childFile : parentContent) {
                    Path name = childFile.toPath();
                    FilePathTreeItem treeNode = new FilePathTreeItem(name);
                    if (childFile.isDirectory()) {
                        treeNode.setExpanded(false);
                        parentNode.getChildren().add(treeNode);
                        getSubtree(treeNode, childFile);
                    } else {
                        parentNode.getChildren().add(treeNode);
                    }
                }
            }
        }
    }

    @Override
    public Parent getRoot() {
        return fileBrowser;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return JavaFxUtils.RegisteredScreen.LOGIN_PAGE;
    }

    private double getPercentageOfAvailableStorageSpace(){
        double availableStorage = localStorage.getAvailableStorageSpace();
        double maxStorage = localStorage.getMaxStorage();
        double progress = (availableStorage/maxStorage);
        return progress;
    }
}
