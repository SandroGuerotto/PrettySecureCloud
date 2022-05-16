package ch.psc.gui.components.signUp;

import java.util.Arrays;
import java.util.List;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import ch.psc.gui.Config;
import ch.psc.gui.components.validator.CompareInputValidator;
import ch.psc.gui.components.validator.EmailValidator;
import ch.psc.gui.components.validator.PasswordValidator;
import ch.psc.gui.components.validator.RequiredInputValidator;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Handles all user data to create an account. An account consists of: username, email address and
 * password.
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
    this.setSpacing(35);
    this.setPadding(new Insets(10, 20, 10, 20));

    usernameField.setLabelFloat(true);
    usernameField.setPromptText(Config.getResourceText("signup.prompt.username"));

    usernameField.getValidators().addAll(
        new RequiredInputValidator(Config.getResourceText("signup.errorLabel.usernameRequired")));
    usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.equals(oldValue))
        usernameField.validate();
    });

    emailTextField.setLabelFloat(true);
    emailTextField.setPromptText(Config.getResourceText("signup.prompt.email"));

    emailTextField.getValidators().addAll(
        new RequiredInputValidator(Config.getResourceText("signup.errorLabel.emailRequired")),
        new EmailValidator(Config.getResourceText("signup.errorLabel.emailNotValid")));
    emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.equals(oldValue))
        emailTextField.validate();
    });

    passwordTextField.setLabelFloat(true);
    passwordTextField.setPromptText(Config.getResourceText("signup.prompt.enterPassword"));

    passwordTextField.getValidators().addAll(
        new RequiredInputValidator(Config.getResourceText("signup.errorLabel.passwordRequired")),
        new PasswordValidator(Config.getResourceText("signup.errorLabel.passwordNotValid")));
    passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.equals(oldValue))
        passwordTextField.validate();
    });

    passwordConfirmTextField.setLabelFloat(true);
    passwordConfirmTextField.setPromptText(Config.getResourceText("signup.prompt.confirmPassword"));

    passwordConfirmTextField.getValidators().addAll(
        new RequiredInputValidator(Config.getResourceText("signup.errorLabel.passwordRequired")),
        new CompareInputValidator(
            Config.getResourceText("signup.errorLabel.confirmationPasswordNotIdentical"),
            passwordTextField));
    passwordConfirmTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.equals(oldValue))
        passwordConfirmTextField.validate();
    });

    Label title = new Label(Config.getResourceText("signup.title.createAccount"));

    this.getChildren().addAll(title, usernameField, emailTextField, passwordTextField,
        passwordConfirmTextField);
    this.setMinHeight(250);
  }

  /**
   * Checks if registration data is valid.
   * 
   * @return true, if registration data meets validation criteria.
   */
  @Override
  public boolean isValid() {
    boolean isValid = emailTextField.validate();
    isValid = passwordTextField.validate() && isValid;
    isValid = usernameField.validate() && isValid;
    isValid = passwordConfirmTextField.validate() && isValid;
    return isValid;
  }

  /**
   * Clears register data and switches back to log in screen.
   */
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
    return Arrays.asList(usernameField.getText(), emailTextField.getText(),
        passwordTextField.getText());
  }
}
