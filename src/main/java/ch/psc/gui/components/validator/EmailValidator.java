package ch.psc.gui.components.validator;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

import java.util.regex.Pattern;

/**
 * Validatorclass for the purpose to validate text fields that require an email address of the user.
 * @author bananasprout
 */

public class EmailValidator extends ValidatorBase {

    private final static String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private final Pattern regexPatternCompiled;


    public EmailValidator(String emailNotValidMessage) {
        super(emailNotValidMessage);
        this.regexPatternCompiled = Pattern.compile(EMAIL_PATTERN);
    }


    @Override
    protected void eval() {
        if (srcControl.get() instanceof TextInputControl) {
            evalTextInputField();
        }
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) srcControl.get();
        hasErrors.set(!regexPatternCompiled.matcher(textField.getText()).matches());
    }

}
