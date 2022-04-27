package ch.psc.gui;

import ch.psc.exceptions.ScreenSwitchException;
import ch.psc.gui.components.validator.EmailValidator;
import ch.psc.gui.components.validator.RegexValidator;
import ch.psc.gui.util.JavaFxUtils;
import ch.psc.presentation.Config;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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
        //Textfelder aus FXML mitgeben
        //Pane alle Children mitgeben. Textfelder rauslöschen (Alle Elemente ausser Textfielder)
        //addIconBefore in Liste mit Index, damit neue Textfelder an richtiger Stelle sind

        RequiredFieldValidator inputMailValidator = new RequiredFieldValidator();
        inputMailValidator.setMessage(Config.getResourceText("login.errorLabel.emailRequired"));
        //inputMailValidator.setIcon(new FontAwesomeIconView(FontAwesomeIcon.WARNING));
        enterMailTextfield.getValidators().add(inputMailValidator);
        enterMailTextfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> o, Boolean oldVal, Boolean newValue) {
                if (!newValue) enterMailTextfield.validate(); //wenn kein neuer Value, dann validate
            }
        });

        RequiredFieldValidator inputPasswordValidator = new RequiredFieldValidator();
        inputPasswordValidator.setMessage(Config.getResourceText("login.errorLabel.passwordRequired"));
        enterPasswordTextfield.getValidators().add(inputPasswordValidator);
        enterPasswordTextfield.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue) enterPasswordTextfield.validate();
            }
        });
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
    private void login(){
        //if(EmailValidator.getInstance().isValid(enterMailTextfield.getText())){}
            EmailValidator emailValidator = new EmailValidator(Config.getResourceText("login.errorLabel.emailNotValid"));
            enterMailTextfield.getValidators().add(emailValidator);
            if (enterMailTextfield.validate()){
                enterPasswordTextfield.getText();
                //Todo: Validation of login, if not valid -> errormessage
            }

    }

}
