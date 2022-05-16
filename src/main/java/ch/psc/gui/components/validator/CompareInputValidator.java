package ch.psc.gui.components.validator;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

/**
 * Compares to string inputs.
 *
 * @author bananasprout
 */

public class CompareInputValidator extends ValidatorBase {

  private final TextInputControl comparingText;

  public CompareInputValidator(String message, TextInputControl comparingText) {
    super(message);
    this.comparingText = comparingText;
  }

  @Override
  protected void eval() {
    if (srcControl.get() instanceof TextInputControl) {
      evalTextInputField();
    }
  }

  private void evalTextInputField() {
    TextInputControl textField = (TextInputControl) srcControl.get();
    hasErrors.set(!textField.getText().equals(comparingText.getText()));
  }

}
