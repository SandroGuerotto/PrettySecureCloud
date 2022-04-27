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
    private String requiredEmailMessage;
    private String emailNotValidMessage;


    public EmailValidator(String requiredEmailMessage, String emailNotValidMessage) {
        this.requiredEmailMessage = requiredEmailMessage;
        this.emailNotValidMessage = emailNotValidMessage;
        this.regexPatternCompiled = Pattern.compile(EMAIL_PATTERN);
    }
    public EmailValidator(String emailNotValidMessage) {
        this.emailNotValidMessage = emailNotValidMessage;
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
        if (textField.getText() == null || textField.getText().isEmpty()) {
            setMessage(requiredEmailMessage);
            //System.out.println(getMessage());
            hasErrors.set(true);
            //System.out.println("1E");

        } else if(!regexPatternCompiled.matcher(textField.getText()).matches()) {
            setMessage(emailNotValidMessage);
            //System.out.println(getMessage());
            hasErrors.set(true);
            //System.out.println("2E");
            } else{
                hasErrors.set(false);
            //System.out.println("3E");
        }
    }

    public String getRegexPattern() {
        return EMAIL_PATTERN;
    }

    @Override
    public void setMessage(String message) {
        this.message.set(message);
    }
}
