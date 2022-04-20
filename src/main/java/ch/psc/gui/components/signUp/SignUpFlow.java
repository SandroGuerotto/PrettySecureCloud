package ch.psc.gui.components.signUp;

import java.util.List;

/**
 * Defines interface to work with the sign-up flow controller.
 * @author SandroGuerotto
 */
public interface SignUpFlow {
    /**
     * Collect all input data.
     *
     * @return user input
     */
    List<Object> getData();

    /**
     * Validates user input.
     *
     * @return true, if input is valid
     */
    boolean isValid();

    /**
     * Clears all data.
     */
    void clear();
}
