package ch.psc.gui;

import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.util.JavaFxUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Super class for all controllers that use the same primary stage
 *
 * <b>Note: In order for the {@code ControlledScreen} to be registered correctly, an entry must be made in the enum {@code RegisteredScreen}
 *
 * @author sevimrid
 */
public abstract class ControlledScreen {
    protected final Stage primaryStage;
    protected JavaFxUtils.RegisteredScreen previousScreen;
    private final Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens;

    protected ControlledScreen(Stage primaryStage, Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens) {
        this.primaryStage = primaryStage;
        this.screens = screens;
    }

    /**
     * Changes the scene in the primaryStage
     *
     * @param newScreen the new scene to be displayed
     * @param params parameters which should be given to the controller
     * @throws ScreenSwitchException is thrown if there is a problem initializing the controller
     */
    public void switchScreen(JavaFxUtils.RegisteredScreen newScreen, Object... params) throws ScreenSwitchException {
        ControlledScreen newScreenController = screens.get(newScreen);
        //initialize the view and pass parameters
        if (newScreenController.init(getScreen(), params)) {
            getRoot().getScene().setRoot(newScreenController.getRoot());
        } else {
            throw new ScreenSwitchException(getScreen(), newScreen);
        }
    }

    /**
     * Called as soon as this screen is displayed if the method is not overridden,
     * only the previous screen and the window title are set
     *
     * @param previousScreen Controller that invoked the new screen
     * @param params parameters needed to initialize the view
     * @return returns true if initialization was successful
     */
    protected boolean init(JavaFxUtils.RegisteredScreen previousScreen, Object... params) {
        this.previousScreen = previousScreen;
        primaryStage.setTitle(getScreen().getTitel());
        return true;
    }

    /**
     * Set the map with all parent elements
     *
     * @param screens map which includes all controllable windows
     */
//    public void setScreens(Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens) {
//        this.screens = screens;
//    }

    /**
     * Sets the primaryStage
     *
     * @param primaryStage primaryStage which is displayed
     */
//    public void setPrimaryStage(Stage primaryStage) {
//        this.primaryStage = primaryStage;
//    }

    /**
     * Root element in fxml file
     *
     * @return root element in fxml file
     */
    protected abstract Parent getRoot();

    /**
     * The configuration for the controller which was registered in the config file
     *
     * @return the screen configuration
     */
    protected abstract JavaFxUtils.RegisteredScreen getScreen();

}
