package ch.psc.exceptions;

import ch.psc.gui.util.JavaFxUtils;
import ch.psc.gui.Config;


/**
 * Exception in case an error occurs when changing screens
 *
 * @author sevimrid, SandroGuerotto
 */
public class ScreenSwitchException extends Exception {

    /**
     * Generates a new ScreenSwitchException
     *
     * @param currentScreen current screen
     * @param newScreen    new screen
     */
    public ScreenSwitchException(JavaFxUtils.RegisteredScreen currentScreen, JavaFxUtils.RegisteredScreen newScreen) {
        super(String.format(Config.getResourceText("screenChangeException.errorMessage"), currentScreen.name(), newScreen.name()));
    }
}

