package ch.psc.exceptions;

import ch.psc.gui.Config;
import ch.psc.gui.util.JavaFxUtils;


/**
 * Exception in case an error occurs when changing screens
 *
 * @author sevimrid, SandroGuerotto
 */
public class ScreenSwitchException extends Exception {

  /**
  * 
  */
  private static final long serialVersionUID = 8394666211251679384L;

  /**
   * Generates a new ScreenSwitchException
   *
   * @param currentScreen current screen
   * @param newScreen new screen
   */
  public ScreenSwitchException(JavaFxUtils.RegisteredScreen currentScreen,
      JavaFxUtils.RegisteredScreen newScreen) {
    super(String.format(Config.getResourceText("screenChangeException.errorMessage"),
        currentScreen.name(), newScreen.name()));
  }
}

