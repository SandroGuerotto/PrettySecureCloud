package ch.psc.gui;

import ch.psc.domain.user.AuthService;
import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.util.JavaFxUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Controller for the LoginView.
 * Contains everything the controller has to reach in the login view and all methods the login view calls based on events.
 *
 * @author waldbsaf
 * @version 1.0
 */

public class LoginController extends ControlledScreen {

    private AuthService authService;

    @FXML
    private TextField enterMailTextfield;

    @FXML
    private PasswordField enterPasswordTextfield;

    @FXML
    private HBox loginPane;

    public LoginController(Stage primaryStage, Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens) {

        super(primaryStage, screens);
    }

    @Override
    public Parent getRoot() {
        return loginPane;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return JavaFxUtils.RegisteredScreen.LOGIN_PAGE;
    }


    @FXML
    public void initialize() {

    }

    /**
     * Register screen will be shown.
     */
    @FXML
    private void register() {
        enterMailTextfield.clear();
        enterPasswordTextfield.clear();

        try {
            switchScreen(JavaFxUtils.RegisteredScreen.SIGNUP_PAGE);
        } catch (ScreenSwitchException e) {
            e.printStackTrace(); //Fehlermeldung an GUI mitgeben
        }
    }

    /**
     * Logindata of user will be checked and if approved the filebrowser will be shown.
     */
    @FXML
    private void login() {
        enterMailTextfield.getText();
        enterPasswordTextfield.getText();
        //Todo: Validation of login
//        User.setUser(authService.authticate("blasd", "gblaksedf"));
        // authService.auth(user) -> user
//        StorageManager.destroy();
//        StorageManager.getInstance().initialize(user);

    }

}
