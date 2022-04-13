package ch.psc.gui.components.signUp;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

public class CreateAccount extends VBox implements SignUpFlow {
    private final TextField usernameField;
    private final TextField emailTextField;
    private final PasswordField passwordTextField;
    private final PasswordField passwordConfirmTextField;

    public CreateAccount() {
        usernameField = new TextField();
        emailTextField = new TextField();
        passwordTextField = new PasswordField();
        passwordConfirmTextField = new PasswordField();
        Label title = new Label("Setup your account");

        setup();
        this.getChildren().addAll(title,usernameField,emailTextField, passwordTextField, passwordConfirmTextField);
        this.setMinHeight(250);
    }

    private void setup() {
        this.setSpacing(15);
        this.setPadding(new Insets(10, 20, 10, 20));
        usernameField.setPromptText("user name");
        emailTextField.setPromptText("user@email.com");

        passwordTextField.setPromptText("Enter your password");
        passwordConfirmTextField.setPromptText("Confirm your password");
    }

    @Override
    public boolean isValid() {
        return !validateIsEmpty(usernameField)
                && !validateIsEmpty(emailTextField) // TODO proper validation
                && isPasswordValid();
    }

    private boolean isPasswordValid() {

        if (validateIsEmpty(passwordTextField)) return false;
        if (validateIsEmpty(passwordConfirmTextField)) return false;


        passwordConfirmTextField.getStyleClass().remove("error");
        if (!passwordTextField.getText().equals(passwordConfirmTextField.getText())){
            passwordConfirmTextField.getStyleClass().add("error");
            return false;
        }
        return true;
    }

    private boolean validateIsEmpty(TextField field) {
        field.getStyleClass().remove("error");
        if (field.getText().isEmpty()){
            field.getStyleClass().add("error");
            return true;
        }
        return false;
    }

    @Override
    public List<Object> getData() {
        return Arrays.asList(usernameField.getText(),emailTextField.getText(), passwordTextField.getText());
    }
}
