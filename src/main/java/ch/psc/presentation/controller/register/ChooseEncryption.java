package ch.psc.presentation.controller.register;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ChooseEncryption extends VBox implements RegisterFlow{
    public ChooseEncryption() {
        //TODO build cipher keys

        Label title = new Label("Choose your encryption");
        this.getChildren().add(title);
        this.setMinHeight(250);
        this.setSpacing(50);
        this.setPadding(new Insets(10, 20, 10, 20));
    }

    @Override
    public List<Object> getData() {
        return new ArrayList<>();
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
