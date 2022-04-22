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

/**
 * Controls the flow of sign-up process. By calling {@link #next()} or {@link #previous()} the current position gets changed.
 * If the flow is at the end (size of {@link #flow}, {@link #isDone} is set to true.
 * If the flow is at the start (-1), {@link #isCanceled} is set to true.
 *
 * @author SandroGuerotto
 */
public class SignUpFlowControl {

    private final List<SignUpFlow> flow;
    private final IntegerProperty currentPosition;
    private final BooleanProperty isDone;
    private final BooleanProperty isCanceled;

    /**
     * Creates sign up flow controller.
     */
    public SignUpFlowControl() {
        flow = new ArrayList<>(Arrays.asList(
                new CreateAccount(),
                new ChooseEncryption(),
                new ChooseStorageService()
        ));
        currentPosition = new SimpleIntegerProperty(-1);
        isDone = new SimpleBooleanProperty(false);
        isDone.bind(currentPosition.isEqualTo(flow.size()));
        isCanceled = new SimpleBooleanProperty(false);
        isCanceled.bind(currentPosition.isEqualTo(-1));
    }

    /**
     * Increases the current position by 1. Maximum size of {@link #flow}.
     */
    public void next() {
        currentPosition.set(Math.min(flow.size(), currentPosition.getValue() + 1));
    }

    /**
     * Decreases the current position by 1. Minimum -1.
     */
    public void previous() {
        currentPosition.set(Math.max(-1, currentPosition.getValue() - 1));
    }

    /**
     * Combines all input data from all registered {@link SignUpFlow} into a list.
     * Used to collect the form data after validation.
     *
     * @return user input
     */
    public List<Object> getData() {
        return flow.stream()
                .map(SignUpFlow::getData)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
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

    public boolean isDone() {
        return isDone.get();
    }

    public BooleanProperty isCanceledProperty() {
        return isCanceled;
    }

    public boolean isCanceled() {
        return isCanceled.get();
    }

}
