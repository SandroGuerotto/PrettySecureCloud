package ch.psc.gui;

import ch.psc.domain.common.context.UserContext;
import ch.psc.domain.user.AuthenticationService;
import ch.psc.domain.user.User;
import ch.psc.exceptions.AuthenticationException;
import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.components.validator.EmailValidator;
import ch.psc.gui.components.validator.PasswordValidator;
import ch.psc.gui.components.validator.RequiredInputValidator;
import ch.psc.gui.util.JavaFxUtils;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Controller for the LoginView.
 * Contains everything the controller has to reach in the login view and all methods the login view calls based on events.
 *
 * @author bananasprout
 */

public class LoginController extends ControlledScreen {

    private final AuthenticationService authenticationService;

    @FXML
    private Label loginErrorLabel;

    @FXML
    private JFXTextField enterMailTextfield;

    @FXML
    private JFXPasswordField enterPasswordTextfield;

    @FXML
    private HBox loginPane;

    public LoginController(Stage primaryStage, Map<JavaFxUtils.RegisteredScreen, ControlledScreen> screens, AuthenticationService authenticationService) {

        super(primaryStage, screens);
        this.authenticationService = authenticationService;
    }

    @Override
    public Parent getRoot() {
        return loginPane;
    }

    @Override
    protected JavaFxUtils.RegisteredScreen getScreen() {
        return JavaFxUtils.RegisteredScreen.LOGIN_PAGE;
    }

    @Override
    protected boolean init(JavaFxUtils.RegisteredScreen previousScreen, Object... params) {
        primaryStage.setMinHeight(Config.MIN_HEIGHT);
        primaryStage.setMinWidth(Config.MIN_WIDTH);
        return super.init(previousScreen, params);
    }

    /**
     * Initializes validation for login data fields.
     */
    @FXML
    public void initialize() {

        enterMailTextfield.setLabelFloat(true);
        enterMailTextfield.getValidators().addAll(
                new RequiredInputValidator(Config.getResourceText("login.errorLabel.emailRequired")),
                new EmailValidator(Config.getResourceText("login.errorLabel.emailNotValid"))
        );

        enterMailTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                enterMailTextfield.validate();
            }
        });

        enterPasswordTextfield.setLabelFloat(true);
        enterPasswordTextfield.getValidators().addAll(
                new RequiredInputValidator(Config.getResourceText("login.errorLabel.passwordRequired")),
                new PasswordValidator(Config.getResourceText("login.errorLabel.passwordNotValid"))
        );
        enterPasswordTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) enterPasswordTextfield.validate();
        });
    }

    /**
     * Clears login data and switches to register screen.
     */
    @FXML
    private void register() {
        enterMailTextfield.clear();
        enterPasswordTextfield.clear();
        enterPasswordTextfield.resetValidation();
        enterMailTextfield.resetValidation();

        try {
            switchScreen(JavaFxUtils.RegisteredScreen.SIGNUP_PAGE);
        } catch (ScreenSwitchException e) {
            loginErrorLabel.setText(Config.getResourceText("login.error.loadingScreenFailed"));
            e.printStackTrace();
        }
    }

    /**
     * Login data of user will be checked and if approved the filebrowser will be shown.
     */
    @FXML
    private void login() {
        if (enterMailTextfield.validate() && enterPasswordTextfield.validate()) {
            try {
                User user = authenticationService.authenticate(enterMailTextfield.getText(), enterPasswordTextfield.getText());
                UserContext.setAuthorizedUser(user);
                switchScreen(JavaFxUtils.RegisteredScreen.FILE_BROWSER_PAGE);
            } catch (AuthenticationException e) {
                setErrorText(Config.getResourceText("login.error.loginFailed"));
                e.printStackTrace();
            } catch (ScreenSwitchException e) {
                setErrorText(Config.getResourceText("login.error.loadingScreenFailed"));
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets a given message to error label
     *
     * @param errorText message
     */
    private void setErrorText(String errorText) {
        loginErrorLabel.setText(errorText);
    }

}
