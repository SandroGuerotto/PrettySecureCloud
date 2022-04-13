package ch.psc.gui.components.signUp;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SignUpFlowControl {

    private final List<SignUpFlow> flow;
    private final IntegerProperty currentPosition;
    private final BooleanProperty isDone;

    public SignUpFlowControl() {
        flow = new ArrayList<>(Arrays.asList(
                new CreateAccount(),
                new ChooseEncryption(),
                new ChooseStorageService()
        ));
        currentPosition = new SimpleIntegerProperty(-1);
        isDone = new SimpleBooleanProperty(false);
    }

    public void next() {
        currentPosition.set(Math.min(flow.size(), currentPosition.getValue() + 1));
        isDone.bind(currentPosition.isEqualTo(flow.size()));
    }

    public void previous() {
        currentPosition.set(Math.max(0, currentPosition.getValue() - 1));
    }

    public int getSteps() {
        return flow.size();
    }

    public IntegerProperty getCurrentPosition() {
        return currentPosition;
    }

    public BooleanProperty isDoneProperty() {
        return isDone;
    }

    public Pane getCurrentPane() {
        return (Pane) flow.get(currentPosition.get());
    }

    public boolean isValid() {
        return flow.get(currentPosition.get()).isValid();
    }

    public List<Object> getData() {
        return flow.stream()
                .map(SignUpFlow::getData)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public boolean isDone() {
        return isDone.get();
    }
}
