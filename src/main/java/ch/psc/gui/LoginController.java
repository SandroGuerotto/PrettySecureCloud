package ch.psc.gui;

import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.components.validator.EmailValidator;
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
import java.util.function.Consumer;

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


    @FXML
    public void initialize(){

        enterMailTextfield.setLabelFloat(true);

        RequiredInputValidator inputMailValidator = new RequiredInputValidator(Config.getResourceText("login.errorLabel.emailRequired"));
        //inputMailValidator.setIcon(new FontAwesomeIconView(FontAwesomeIcon.WARNING));
        enterMailTextfield.getValidators().add(inputMailValidator);
        enterMailTextfield.focusedProperty().addListener((o, oldVal, newValue) -> {
            if (!newValue) enterMailTextfield.validate(); //wenn kein neuer Value, dann validate
        });
        enterMailTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)){
                enterMailTextfield.validate();
            }
        });

        enterPasswordTextfield.setLabelFloat(true);

        RequiredInputValidator inputPasswordValidator = new RequiredInputValidator(Config.getResourceText("login.errorLabel.passwordRequired"));
        enterPasswordTextfield.getValidators().add(inputPasswordValidator);
        enterPasswordTextfield.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) enterPasswordTextfield.validate();
        });
        enterPasswordTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)){
                enterPasswordTextfield.validate();
            }
        });
    }

    /**
     * Register screen will be shown.
     */
    @FXML
    private void register() {
        enterPasswordTextfield.resetValidation();
        enterMailTextfield.resetValidation();
        enterMailTextfield.clear();
        enterPasswordTextfield.clear();

        try {
            switchScreen(JavaFxUtils.RegisteredScreen.SIGNUP_PAGE);
        } catch (ScreenSwitchException e) {
            e.printStackTrace(); //TODO: Fehlermeldung an GUI mitgeben
        }
    }

    /**
     * Logindata of user will be checked and if approved the filebrowser will be shown.
     */
    @FXML
    private void login(){
            EmailValidator emailValidator = new EmailValidator(Config.getResourceText("login.errorLabel.emailNotValid"));
            enterMailTextfield.getValidators().add(emailValidator);
            if (enterMailTextfield.validate()){
                enterMailTextfield.getText();
                enterPasswordTextfield.getText();
                //Todo: Validation of login, if not valid -> errormessage
                loginErrorLabel.setText("TODO: login / password is wrong resp. you have to sign up first");
            }

    }

}
