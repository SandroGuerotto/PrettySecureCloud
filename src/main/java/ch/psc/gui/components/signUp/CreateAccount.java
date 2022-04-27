package ch.psc.gui.components.signUp;

import ch.psc.gui.components.validator.*;
import ch.psc.gui.util.JavaFxUtils;
import ch.psc.presentation.Config;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

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
    private final Label errorLabel;

    /**
     * Creates an account creation screen.
     */
    public CreateAccount() {
        usernameField = new JFXTextField();
        emailTextField = new JFXTextField();
        passwordTextField = new JFXPasswordField();
        passwordConfirmTextField = new JFXPasswordField();
        errorLabel = new Label();

        initialize();
    }

    /**
     * Initializes form and creates input field for creating a new account.
     */
    private void initialize() {
        this.setSpacing(30); //TODO: make appearance of textfields unified (in login and signup window)
        this.setPadding(new Insets(10, 20, 10, 20));

        usernameField.setPromptText(Config.getResourceText("signup.prompt.username"));
        //Input for username field required
        RequiredInputValidator requiredUserNameValidator = new RequiredInputValidator(Config.getResourceText("signup.errorLabel.usernameRequired"));
        usernameField.getValidators().add(requiredUserNameValidator);
        usernameField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) usernameField.validate(); //wenn kein neuer Value, dann validate
            }
        });

        emailTextField.setPromptText(Config.getResourceText("signup.prompt.email"));
        //Email address field should not be empty, and should be valid
        EmailValidator emailValidator = new EmailValidator(Config.getResourceText("signup.errorLabel.emailRequired"), Config.getResourceText("signup.errorLabel.emailNotValid"));
        emailTextField.getValidators().add(emailValidator);
        emailTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue) emailTextField.validate(); //wenn kein neuer Value, dann validate

            }
        });
        //For Testing Purposes
        /*
        emailTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue == "" ||!newValue.equals(oldValue)){
                    emailTextField.validate();
                }
            }
        });

         */
        passwordTextField.setPromptText(Config.getResourceText("signup.prompt.enterPassword"));
        PasswordValidator passwordValidator = new PasswordValidator(Config.getResourceText("signup.errorLabel.passwordRequired"), Config.getResourceText("signup.errorLabel.passwordNotValid"));
        passwordTextField.getValidators().add(passwordValidator);
        passwordTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue == "" ||!newValue.equals(oldValue)){
                    passwordTextField.validate();
                }
            }
        });

        passwordConfirmTextField.setPromptText(Config.getResourceText("signup.prompt.confirmPassword"));

        CompareInputValidator compareInputValidator = new CompareInputValidator(Config.getResourceText("signup.errorLabel.confirmationPasswordNotIdentical"));
        passwordConfirmTextField.getValidators().add(compareInputValidator);
        passwordConfirmTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
                if(!passwordValidator.getHasErrors()){
                    compareInputValidator.setComparingText(passwordTextField.getText());
                    passwordConfirmTextField.validate();
                }
            }
        });

        Label title = new Label(Config.getResourceText("signup.title.createAccount"));
        errorLabel.setVisible(false);

        this.getChildren().addAll(title,
                JavaFxUtils.addIconBefore(usernameField, FontAwesomeIcon.USER, "signup-icon"),
                JavaFxUtils.addIconBefore(emailTextField, FontAwesomeIcon.ENVELOPE, "signup-icon"),
                JavaFxUtils.addIconBefore(passwordTextField, FontAwesomeIcon.KEY, "signup-icon"),
                JavaFxUtils.addIconBefore(passwordConfirmTextField, FontAwesomeIcon.KEY, "signup-icon"),
                errorLabel
        );
        this.setMinHeight(250);
    }

    @Override
    public boolean isValid() {
       return !emailTextField.getActiveValidator().getHasErrors() ||
               !passwordTextField.getActiveValidator().getHasErrors() ||
               !passwordConfirmTextField.getActiveValidator().getHasErrors() ||
               !usernameField.getActiveValidator().getHasErrors();

  /*      return !validateIsEmpty(usernameField)
                && !validateIsEmpty(emailTextField) // TODO proper validation: error message
                && isPasswordValid();*/
    }

    @Override
    public void clear() {
        usernameField.clear();
        emailTextField.clear();
        passwordConfirmTextField.clear();
        passwordTextField.clear();
    }

    @Override
    public List<Object> getData() {
        return Arrays.asList(usernameField.getText(), emailTextField.getText(), passwordTextField.getText());
    }
}
