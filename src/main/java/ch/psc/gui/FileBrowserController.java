package ch.psc.gui;

import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.util.JavaFxUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.input.TransferMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Stream;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ch.psc.gui.components.fileBrowser.FilePathTreeItem;
import static java.util.logging.Level.SEVERE;

/**
 * Controller for the LoginView.
 * Contains everything the controller has to reach in the login view and all methods the login view calls based on events.
 *
 * @author waldbsaf
 * @version 1.0
 */

public class FileBrowserController extends ControlledScreen {

    @FXML
    private BorderPane fileBrowser;

    @FXML
    private TextArea textArea;

    @FXML
    private TreeView treeView;


    public FileBrowserController(Stage primaryStage, Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens) {
        super(primaryStage, screens);
    }

    public void initialize(){
        Node node = textArea;
        node.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
        });

        node.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if(event.getDragboard().hasFiles()){
                File fileToLoad = db.getFiles().get(0); //get files from dragboard
                Task loadFiles = fileLoaderTask(fileToLoad);      //create asynch task to load files
                loadFiles.run();                        //load file in
            }
        });
        File[] paths = File.listRoots();
        String path = paths[0].toString();
        //displayTreeView("C:\\Something");

        String hostName = "computer";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException x) {
        }
        TreeItem<String> rootNode = new TreeItem<>(hostName,new ImageView(new Image(ClassLoader.getSystemResourceAsStream("images/fileBrowser/computer.png"))));
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
        File fileInputDirectoryLocation = new File("C:\\ZHAW\\");
        File fileList[] = fileInputDirectoryLocation.listFiles();
        for(File file : fileList){
            Path name = file.toPath();
            FilePathTreeItem treeNode = new FilePathTreeItem(name);
            rootNode.getChildren().add(treeNode);
        }

        rootNode.setExpanded(true);
        treeView.setRoot(rootNode);

    }

    public static void createTree(File file, CheckBoxTreeItem<String> parent) {
        if (file.isDirectory()) {
            CheckBoxTreeItem<String> treeItem = new CheckBoxTreeItem<>(file.getName());
            parent.getChildren().add(treeItem);
            for (File f : file.listFiles()) {
                createTree(f, treeItem);
            }
            parent.getChildren().add(new CheckBoxTreeItem<>(file.getName()));
        }
    }

    public void displayTreeView(String inputDirectoryLocation) {

        //=====================================================

//        // Creates the root item.
//        CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>(inputDirectoryLocation);
//
//        // Hides the root item of the tree view.
//        treeView.setShowRoot(false);
//
//        // Creates the cell factory.
//        treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
//
//        // Get a list of files.
//        File fileInputDirectoryLocation = new File(inputDirectoryLocation);
//        File fileList[] = fileInputDirectoryLocation.listFiles();
//
//        // create tree
//        for (File file : fileList) {
//            createTree(file, rootItem);
//        }
//
//        treeView.setRoot(rootItem);
    }

    @Override
    public Parent getRoot() {
        return fileBrowser;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return JavaFxUtils.RegisteredScreen.LOGIN_PAGE;
    }


    private Task<String> fileLoaderTask(File fileToLoad){
        //Create a task to load the file asynchronously
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                BufferedReader reader = new BufferedReader(new FileReader(fileToLoad));

                //Use Files.lines() to calculate total lines - used for progress
                long lineCount;
                try (Stream<String> stream = Files.lines(fileToLoad.toPath())) {
                    lineCount = stream.count();
                }

                //Load in all lines one by one into a StringBuilder separated by "\n" - compatible with TextArea
                String line;
                StringBuilder totalFile = new StringBuilder();
                long linesLoaded = 0;
                while((line = reader.readLine()) != null) {
                    totalFile.append(line);
                    totalFile.append("\n");
                    updateProgress(++linesLoaded, lineCount);
                }

                return totalFile.toString();
            }
        };

        //If successful, update the text area, display a success message and store the loaded file reference
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                textArea.setText(loadFileTask.get());
//                loadedFileReference = fileToLoad;
            } catch (InterruptedException | ExecutionException e) {
                Logger.getLogger(getClass().getName()).log(SEVERE, null, e);
                textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            }
        });

        //If unsuccessful, set text area with error message and status message to failed
        loadFileTask.setOnFailed(workerStateEvent -> {
            textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
        });

        return loadFileTask;
    }

}
