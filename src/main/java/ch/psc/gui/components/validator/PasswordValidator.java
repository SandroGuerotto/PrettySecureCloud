package ch.psc.gui.components.validator;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

import java.util.regex.Pattern;

public class PasswordValidator extends ValidatorBase {

    //Regexpatter: contain at least one digit from 0-9, spaces are not allowed, at least 8 characters and at most 20
    private final static String PASSWORD_PATTERN = "^(?=. *[0-9])" + "(?=\\S+$).{8,20}$";
    private Pattern regexPatternCompiled;
    private String requiredPasswordMessage;
    private String passwordNotValidMessage;


    public PasswordValidator(String requiredPasswordMessage, String passwordNotValidMessage) {
        this.requiredPasswordMessage = requiredPasswordMessage;
        this.passwordNotValidMessage = passwordNotValidMessage;
        this.regexPatternCompiled = Pattern.compile(PASSWORD_PATTERN);
    }

    @Override
    protected void eval() {
        if (srcControl.get() instanceof TextInputControl) {
            evalTextInputField();
        }
    }

    /**
     * Checks first if field was left empty.
     * Checks if password matches the criteria of the regex pattern.
     */
    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) srcControl.get();
        if (textField.getText() == null || textField.getText().isEmpty()) {
            hasErrors.set(true);
            setMessage(requiredPasswordMessage);
        } else if (!regexPatternCompiled.matcher(textField.getText()).matches()) {
            hasErrors.set(true);
            setMessage(passwordNotValidMessage);
        } else {
            hasErrors.set(false);
        }
    }

    public String getRegexPattern() {
        return PASSWORD_PATTERN;
    }


}
