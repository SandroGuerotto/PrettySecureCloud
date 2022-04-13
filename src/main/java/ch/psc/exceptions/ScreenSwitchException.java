package ch.psc.exceptions;

import  ch.psc.presentation.Config;
import ch.psc.gui.util.JavaFxUtils;


/**
 * Exception in case an error occurs when changing screens
 *
 * @author sevimrid
 */
public class ScreenSwitchException extends Exception {

    /**
     * Generates a new  ScreenSwitchException
     *
     * @param currentScreen current screen
     * @param newScreen    new screen
     */
    public ScreenSwitchException(JavaFxUtils.RegisteredScreen currentScreen, JavaFxUtils.RegisteredScreen newScreen) {
        super(String.format(Config.getResourceText("screenWechselException.fehlerMeldung"), currentScreen.name(), newScreen.name()));
    }
}

