package ch.psc.gui.components.validator;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

public class RequiredInputValidator extends ValidatorBase {

    public RequiredInputValidator(String requiredInputMessage) {
        super(requiredInputMessage);
    }

    @Override
    protected void eval() {
        if (srcControl.get() instanceof TextInputControl) {
            evalTextInputField();
        }
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) srcControl.get();
        hasErrors.set(textField.getText() == null || textField.getText().isEmpty());
    }
}
