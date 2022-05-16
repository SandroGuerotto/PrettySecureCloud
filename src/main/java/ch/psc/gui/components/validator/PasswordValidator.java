package ch.psc.gui.components.validator;

import java.util.regex.Pattern;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

/**
 * Validator for the purpose to validate text fields that require a valid password from the user,
 * matching the criteria of {@link #PASSWORD_PATTERN}.
 *
 * @author bananasprout
 */

public class PasswordValidator extends ValidatorBase {

  // Regexpatter: contain at least one digit from 0-9, spaces are not allowed, at least 8 characters
  // and at most 20
  private final static String PASSWORD_PATTERN = "^(?!.* )(?=.*\\d).{8,20}$";
  private final Pattern regexPatternCompiled;


  public PasswordValidator(String passwordNotValidMessage) {
    super(passwordNotValidMessage);
    this.regexPatternCompiled = Pattern.compile(PASSWORD_PATTERN);
  }

  @Override
  protected void eval() {
    if (srcControl.get() instanceof TextInputControl) {
      evalTextInputField();
    }
  }

  /**
   * Checks first if field was left empty. Checks if password matches the criteria of the regex
   * pattern.
   */
  private void evalTextInputField() {
    TextInputControl textField = (TextInputControl) srcControl.get();
    hasErrors.set(!regexPatternCompiled.matcher(textField.getText()).matches());
  }

}
