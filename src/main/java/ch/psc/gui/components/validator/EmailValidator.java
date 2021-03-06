package ch.psc.gui.components.validator;

import java.util.regex.Pattern;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

/**
 * Validator for the purpose to validate text fields, that require a valid email address from the
 * user, matching the criteria of the {@link #EMAIL_PATTERN}.
 *
 * @author bananasprout
 */

public class EmailValidator extends ValidatorBase {
  private final static String EMAIL_PATTERN =
      "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
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
