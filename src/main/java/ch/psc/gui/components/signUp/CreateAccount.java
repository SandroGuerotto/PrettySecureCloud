package ch.psc.gui.components.signUp;

import ch.psc.presentation.Config;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

/**
 * Handles all user data to create an account.
 * An account consists of: username, email address and password.
 *
 * @author SandroGuerotto
 */
public class CreateAccount extends VBox implements SignUpFlow {
    private final TextField usernameField;
    private final TextField emailTextField;
    private final PasswordField passwordTextField;
    private final PasswordField passwordConfirmTextField;

    /**
     * Creates an account creation screen.
     */
    public CreateAccount() {
        usernameField = new TextField();
        emailTextField = new TextField();
        passwordTextField = new PasswordField();
        passwordConfirmTextField = new PasswordField();

        initialize();
    }

    /**
     * Initializes form and creates input field for creating a new account.
     */
    private void initialize() {
        this.setSpacing(15);
        this.setPadding(new Insets(10, 20, 10, 20));

        usernameField.setPromptText(Config.getResourceText("signup.prompt.username"));
        emailTextField.setPromptText(Config.getResourceText("signup.prompt.email"));

        passwordTextField.setPromptText(Config.getResourceText("signup.prompt.enterPassword"));
        passwordConfirmTextField.setPromptText(Config.getResourceText("signup.prompt.confirmPassword"));

        Label title = new Label(Config.getResourceText("signup.title.createAccount"));

        this.getChildren().addAll(title, usernameField, emailTextField, passwordTextField, passwordConfirmTextField);
        this.setMinHeight(250);
    }

    @Override
    public boolean isValid() {
        return !validateIsEmpty(usernameField)
                && !validateIsEmpty(emailTextField) // TODO proper validation: error message
                && isPasswordValid();
    }

    @Override
    public void clear() {
        usernameField.clear();
        emailTextField.clear();
        passwordConfirmTextField.clear();
        passwordTextField.clear();
    }

    /**
     * Validates entered and confirmed password.
     *
     * @return true, if entered and confirmed are equal and not empty.
     */
    private boolean isPasswordValid() {

        if (validateIsEmpty(passwordTextField)) return false;
        if (validateIsEmpty(passwordConfirmTextField)) return false;


        passwordConfirmTextField.getStyleClass().remove("error");
        if (!passwordTextField.getText().equals(passwordConfirmTextField.getText())) {
            passwordConfirmTextField.getStyleClass().add("error");
            return false;
        }
        return true;
    }

    /**
     * Adds "error" CSS-class if input field is empty
     *
     * @param field input field to check
     * @return true, if not empty
     */
    private boolean validateIsEmpty(TextField field) {
        field.getStyleClass().remove("error");
        if (field.getText().isEmpty()) {
            field.getStyleClass().add("error");
            return true;
        }
        return false;
    }

    @Override
    public List<Object> getData() {
        return Arrays.asList(usernameField.getText(), emailTextField.getText(), passwordTextField.getText());
    }
}
