package ch.psc.gui.components.signUp;

import ch.psc.gui.components.validator.CompareInputValidator;
import ch.psc.gui.components.validator.EmailValidator;
import ch.psc.gui.components.validator.PasswordValidator;
import ch.psc.gui.components.validator.RequiredInputValidator;
import ch.psc.gui.util.JavaFxUtils;
import ch.psc.presentation.Config;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
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
        this.setSpacing(35); //TODO: make appearance of textfields unified (in login and signup window)
        this.setPadding(new Insets(10, 20, 10, 20));

        usernameField.setLabelFloat(true);
        usernameField.setPromptText(Config.getResourceText("signup.prompt.username"));
        //Input for username field required
        RequiredInputValidator requiredInput = new RequiredInputValidator(Config.getResourceText("signup.errorLabel.usernameRequired"));
        usernameField.getValidators().add(requiredInput);
        usernameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) usernameField.validate();
        });
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) usernameField.validate();
        });

        emailTextField.setLabelFloat(true);
        emailTextField.setPromptText(Config.getResourceText("signup.prompt.email"));

        requiredInput = new RequiredInputValidator(Config.getResourceText("signup.errorLabel.emailRequired"));
        EmailValidator emailValidator = new EmailValidator(Config.getResourceText("signup.errorLabel.emailNotValid"));
        emailTextField.getValidators().addAll(requiredInput, emailValidator);
        emailTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) emailTextField.validate();
        });
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) emailTextField.validate();
        });

        passwordTextField.setLabelFloat(true);
        passwordTextField.setPromptText(Config.getResourceText("signup.prompt.enterPassword"));


        requiredInput = new RequiredInputValidator(Config.getResourceText("signup.errorLabel.passwordRequired"));
        PasswordValidator passwordValidator = new PasswordValidator(Config.getResourceText("signup.errorLabel.passwordNotValid"));
        passwordTextField.getValidators().addAll(requiredInput, passwordValidator);
        passwordTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                passwordTextField.validate();
            }
        });
        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) passwordTextField.validate();
        });

        passwordConfirmTextField.setLabelFloat(true);
        passwordConfirmTextField.setPromptText(Config.getResourceText("signup.prompt.confirmPassword"));

        CompareInputValidator compareInputValidator = new CompareInputValidator(Config.getResourceText("signup.errorLabel.confirmationPasswordNotIdentical"), passwordTextField);
        requiredInput = new RequiredInputValidator(Config.getResourceText("signup.errorLabel.passwordRequired"));
        passwordConfirmTextField.getValidators().addAll(requiredInput, compareInputValidator);
        passwordConfirmTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                passwordConfirmTextField.validate();
            }
        });
        passwordConfirmTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordTextField.validate() && !newValue.equals(oldValue)) passwordTextField.validate();
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
        boolean isValid = true;
        if(emailTextField.getActiveValidator() != null ){
            isValid = !emailTextField.getActiveValidator().getHasErrors();
        }
        if(passwordTextField.getActiveValidator() != null){
            isValid = !passwordTextField.getActiveValidator().getHasErrors();
        }
        if(passwordConfirmTextField.getActiveValidator() != null){
            isValid = !passwordConfirmTextField.getActiveValidator().getHasErrors();
        }
        if(usernameField.getActiveValidator() != null){
            isValid = !usernameField.getActiveValidator().getHasErrors();
        }
       return isValid;
    }

    @Override
    public void clear() {
        usernameField.clear();
        usernameField.resetValidation();
        emailTextField.clear();
        emailTextField.resetValidation();
        passwordConfirmTextField.clear();
        passwordConfirmTextField.resetValidation();
        passwordTextField.clear();
        passwordTextField.resetValidation();
    }

    @Override
    public List<Object> getData() {
        return Arrays.asList(usernameField.getText(), emailTextField.getText(), passwordTextField.getText());
    }
}
