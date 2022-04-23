package ch.psc.gui.components.validator;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

import java.util.regex.Pattern;

public class RegexValidator extends ValidatorBase {

    private String regexPattern;

    public RegexValidator(String message) {
        super(message);
    }

    public RegexValidator() {

    }
    private Pattern regexPatternCompiled;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void eval() {
        if (srcControl.get() instanceof TextInputControl) {
            evalTextInputField();
        }
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) srcControl.get();
        if (regexPatternCompiled.matcher(textField.getText()).matches()) {
            hasErrors.set(false);
        } else {
            hasErrors.set(true);
        }
    }

    /*
     * GETTER AND SETTER
     */
    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
        this.regexPatternCompiled = Pattern.compile(regexPattern);
    }

    public String getRegexPattern() {
        return regexPattern;
    }

}
