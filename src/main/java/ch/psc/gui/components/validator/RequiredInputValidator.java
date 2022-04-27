package ch.psc.gui.components.validator;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

import java.util.regex.Pattern;

public class RequiredInputValidator extends ValidatorBase {

    private String requiredInputMessage;

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
        if (textField.getText() == null || textField.getText().isEmpty()) {
            hasErrors.set(true);
        } else {
            hasErrors.set(false);
        }
    }
}
