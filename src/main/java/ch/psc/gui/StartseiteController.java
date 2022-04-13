package ch.psc.gui;

import ch.psc.gui.util.JavaFxUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Controller für die Startseite
 *
 * @author sevimrid
 */
public class StartseiteController extends ControlledScreen {


    @FXML
    private VBox startseite;

    @FXML
    private ProgressBar limiteProgress;

    @FXML
    private Label limiteStart;

    public StartseiteController(Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens,
                                Stage primaryStage) {
        // do something here
        super(primaryStage, screens);
    }

    @FXML
    public void initialize() {
        // just example
        initGuiElements();
    }


    @Override
    protected boolean init(JavaFxUtils.RegisteredScreen vorherigerScreen, Object... params) {
        super.init(vorherigerScreen, params);
        initGuiElements();
        return true;
    }

    private void initGuiElements() {

    }


    @FXML
    private void exampaleMethod1() {

    }

    @FXML
    private void exampaleMethod2() {

    }
    @FXML
    private void exampaleMethod3() {

    }


    @Override
    protected Parent getRoot() {
        return startseite;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return JavaFxUtils.RegisteredScreen.LOGIN_PAGE;
    }
}