package ch.psc.gui;

import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.components.validator.EmailValidator;
import ch.psc.gui.components.validator.PasswordValidator;
import ch.psc.gui.components.validator.RequiredInputValidator;
import ch.psc.gui.util.JavaFxUtils;
import ch.psc.presentation.Config;
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

    @FXML
    private Label loginErrorLabel;

    @FXML
    private JFXTextField enterMailTextfield;

    @FXML
    private JFXPasswordField enterPasswordTextfield;

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

    /**
     * Initializes validation for login data fields.
     *
     */
    @FXML
    public void initialize(){

        enterMailTextfield.setLabelFloat(true);
        //inputMailValidator.setIcon(new FontAwesomeIconView(FontAwesomeIcon.WARNING));
        enterMailTextfield.getValidators().addAll(
                new RequiredInputValidator(Config.getResourceText("login.errorLabel.emailRequired")),
                new EmailValidator(Config.getResourceText("login.errorLabel.emailNotValid"))
                );

        enterMailTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)){
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
            e.printStackTrace(); //TODO: Fehlermeldung an GUI mitgeben
        }
    }

    /**
     * Login data of user will be checked and if approved the filebrowser will be shown.
     */
    @FXML
    private void login(){
            if (enterMailTextfield.validate() && enterPasswordTextfield.validate()){
                enterMailTextfield.getText();
                enterPasswordTextfield.getText();
                //Todo: Validation of login, if not valid -> errormessage
                loginErrorLabel.setText("TODO: login / password is wrong resp. you have to sign up first");
            }

    }

}
