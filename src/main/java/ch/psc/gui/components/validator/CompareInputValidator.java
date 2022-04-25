package ch.psc.gui.components.validator;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

/**
 * Compares to string inputs.
 * @author bananasprout
 */


public class CompareInputValidator extends ValidatorBase {

    public CompareInputValidator(String message) {
        super(message);
    }

    public CompareInputValidator(){

    }

    private String comparingText;

    @Override
    protected void eval() {
        if (srcControl.get() instanceof TextInputControl) {
            evalTextInputField();
        }
    }

    private void evalTextInputField(){
        TextInputControl textField = (TextInputControl) srcControl.get();
        if(textField.getText().equals(comparingText)){
            hasErrors.set(false);
        }else{
            hasErrors.set(true);
        }
    }

    public void setComparingText(String comparingText){
        this.comparingText = comparingText;
    }


}
