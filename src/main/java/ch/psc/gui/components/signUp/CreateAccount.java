package ch.psc.gui.components.signUp;

import ch.psc.gui.components.validator.CompareInputValidator;
import ch.psc.gui.components.validator.RegexValidator;
import ch.psc.gui.util.JavaFxUtils;
import ch.psc.presentation.Config;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

/**
 * Handles all user data to create an account.
 * An account consists of: username, email address and password.
 *
 * @author SandroGuerotto, bananasprout
 */
public class CreateAccount extends VBox implements SignUpFlow {
    private final JFXTextField usernameField;
    private final JFXTextField emailTextField;
    private final JFXPasswordField passwordTextField;
    private final JFXPasswordField passwordConfirmTextField;
    private boolean tempVariableForRefactoring;

    /**
     * Creates an account creation screen.
     */
    public CreateAccount() {
        usernameField = new JFXTextField();
        emailTextField = new JFXTextField();
        passwordTextField = new JFXPasswordField();
        passwordConfirmTextField = new JFXPasswordField();

        initialize();
    }

    /**
     * Initializes form and creates input field for creating a new account.
     */
    private void initialize() {
        this.setSpacing(40);
        this.setPadding(new Insets(10, 20, 10, 20));

        usernameField.setPromptText(Config.getResourceText("signup.prompt.username"));
        RequiredFieldValidator requiredUserNameValidator = new RequiredFieldValidator();
        requiredUserNameValidator.setMessage("Please enter a username");
        usernameField.getValidators().add(requiredUserNameValidator);
        usernameField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) usernameField.validate(); //wenn kein neuer Value, dann validate
            }
        });


        emailTextField.setPromptText(Config.getResourceText("signup.prompt.email"));
        RequiredFieldValidator requiredEmailValidator = new RequiredFieldValidator();
        requiredEmailValidator.setMessage("Please enter an email adress");
        emailTextField.getValidators().add(requiredEmailValidator);
        RegexValidator inputMailValidator = new RegexValidator();
        inputMailValidator.setMessage(Config.getResourceText("login.errorlabel.emailnotvalid"));
        inputMailValidator.setRegexPattern("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

        emailTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) emailTextField.validate(); //wenn kein neuer Value, dann validate
                emailTextField.getValidators().add(inputMailValidator);
            }
        });





        passwordTextField.setPromptText(Config.getResourceText("signup.prompt.enterPassword"));
        RegexValidator passwordValidator = new RegexValidator();
        //Regex, password must contain at least one digit from 0-9, spaces are not allowed, at least 8 characters and at most 20
        passwordValidator.setRegexPattern("^(?=.*[0-9])"+"(?=\\S+$).{8,20}$");
        passwordValidator.setMessage("1 digit, 8 to 20 characters");
        passwordTextField.getValidators().add(passwordValidator);
        passwordTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.equals(oldValue)) passwordTextField.validate();
            }
        });


        passwordConfirmTextField.setPromptText(Config.getResourceText("signup.prompt.confirmPassword"));
        CompareInputValidator compareInputValidator = new CompareInputValidator();
        compareInputValidator.setMessage("your password doesn't match");
        passwordConfirmTextField.getValidators().add(compareInputValidator);
        passwordConfirmTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                compareInputValidator.setComparingText(passwordTextField.getText());
                passwordConfirmTextField.validate();
            }
        });

        Label title = new Label(Config.getResourceText("signup.title.createAccount"));

        this.getChildren().addAll(title,
                JavaFxUtils.addIconBefore(usernameField, FontAwesomeIcon.USER, "signup-icon"),
                JavaFxUtils.addIconBefore(emailTextField, FontAwesomeIcon.ENVELOPE, "signup-icon"),
                JavaFxUtils.addIconBefore(passwordTextField, FontAwesomeIcon.KEY, "signup-icon"),
                JavaFxUtils.addIconBefore(passwordConfirmTextField, FontAwesomeIcon.KEY, "signup-icon")
        );
        this.setMinHeight(250);
    }

    @Override
    public boolean isValid() {
       return !emailTextField.getActiveValidator().getHasErrors() || !passwordTextField.getActiveValidator().getHasErrors();

  /*      return !validateIsEmpty(usernameField)
                && !validateIsEmpty(emailTextField) // TODO proper validation: error message
                && isPasswordValid();*/
    }

    @Override
    public void clear() {
        usernameField.clear();
        removeErrorClass(usernameField);
        emailTextField.clear();
        removeErrorClass(emailTextField);
        passwordConfirmTextField.clear();
        removeErrorClass(passwordConfirmTextField);
        passwordTextField.clear();
        removeErrorClass(passwordTextField);
    }

    /**
     * Validates entered and confirmed password.
     *
     * @return true, if entered and confirmed are equal and not empty.
     */


    private void removeErrorClass(Region region) {
        region.getStyleClass().remove("error");
    }

    private void addErrorClass(Region region) {
        region.getStyleClass().add("error");
        region.requestFocus();
    }

    /**
     * Adds "error" CSS-class if input field is empty
     *
     * @param //field input field to check
     * @return true, if not empty
     */
    /*private boolean validateIsEmpty(JFXTextField field) {
        removeErrorClass(field);
        if (field.getText().isEmpty()) {
            addErrorClass(field);
            return true;
        }
        return false;
    }*/

 /*   private boolean validateIsEmpty(JFXPasswordField field) {
        removeErrorClass(field);
        if (field.getText().isEmpty()) {
            addErrorClass(field);
            return true;
        }
        return false;
    }*/



    @Override
    public List<Object> getData() {
        return Arrays.asList(usernameField.getText(), emailTextField.getText(), passwordTextField.getText());
    }
}
