package ch.psc.gui;

import ch.psc.presentation.Config;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Controller Superclass
 *
 * @author sevimrid
 */
public abstract class Controller{
    protected Stage stage;

    /**
     * Loads the window and intializes the super controller
     *
     * @param fxmlFileName filename
     * @return Controller of FXML files
     * @throws IOException if an error occurs while loading the file.
     */
    protected static Controller loadWindows(String fxmlFileName, Stage owner) throws IOException{
        FXMLLoader loader = new FXMLLoader(
                Controller.class.getResource(fxmlFileName),
                ResourceBundle.getBundle(Config.TEXT_BUNDLE_NAME));

        Pane rootPane = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(rootPane));

        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        Controller controller = loader.getController();
        controller.init(stage);

        return controller;
    }

    /**
     * Initialize Super Controller
     *
     * @param stage current stage
     */
    protected void init(Stage stage){
        this.stage = stage;
        stage.setMinHeight(getMinHeight());
        stage.setMinWidth(getMinWidth());
        stage.setWidth(getMinWidth());
        stage.setHeight(getMinHeight());
        stage.setTitle(Config.getResourceText(getTitelKey()));
    }

    /**
     * Minimum height
     *
     * @return minimum height
     */
    protected abstract int getMinHeight();

    /**
     * Minimum width
     *
     * @return minimum width.
     */
    protected abstract int getMinWidth();

    /**
     * Titel Key for text.properties.
     *
     * @return Titel key.
     */
    protected abstract String getTitelKey();

    /**
     * show Window
     */
    protected void showWindow() {
        stage.show();
    }

    /**
     * show window and wait
     */
    protected void showAndWait() {
        stage.showAndWait();
    }

    /**
     * close window.
     */
    @FXML
    public void cancel() {stage.close();}
}
