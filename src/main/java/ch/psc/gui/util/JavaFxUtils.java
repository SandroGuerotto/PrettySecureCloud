package ch.psc.gui.util;

import java.awt.Desktop;
import java.net.URI;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import ch.psc.gui.Config;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Contains help methods for JavaFx
 *
 * @author sevimrid, SandroGuerotto, bananasprout
 */
public class JavaFxUtils {

  /**
   * No instance necessary
   */
  private JavaFxUtils() {}

  public static Pane addIconBefore(Region field, FontAwesomeIcon icon, String cssClass) {
    FontAwesomeIconView image = new FontAwesomeIconView(icon, "16");
    image.getStyleClass().add(cssClass);
    HBox pane = new HBox(10, image, field);
    HBox.setHgrow(field, Priority.ALWAYS);
    pane.setAlignment(Pos.CENTER_LEFT);
    return pane;
  }

  /**
   * Open url in default browser
   * 
   * @param url the URL to be displayed in the user default browser
   */
  public static void openInBrowser(String url) {
    try {
      Desktop.getDesktop().browse(new URI(url));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param size file size in bytes
   * @return human-readable size
   */
  public static String formatSize(long size) {
    if (size == 0)
      return "";
    if (-1000 < size && size < 1000) {
      return size + " B";
    }
    CharacterIterator ci = new StringCharacterIterator("kMGTPE");
    while (size <= -999_950 || size >= 999_950) {
      size /= 1000;
      ci.next();
    }
    return String.format("%.1f %cB", size / 1000.0, ci.current());
  }

  /**
   * Enum with the information about the controlled screens
   */
  public enum RegisteredScreen {
    // TODo sevimrid , bananasprout, SandroGuerotto, ChrisWals
    LOGIN_PAGE("login.fxml", "login.title"), SIGNUP_PAGE("signUp.fxml",
        "signup.title"), FILE_BROWSER_PAGE("fileBrowser.fxml", "fileBrowser.titel");


    private final String fxmlFileName;
    private final String title;

    /**
     * Set fxmlFileName and title
     *
     * @param fxmlFileName Fxml file name of the controller
     * @param title Title of the window
     */
    RegisteredScreen(final String fxmlFileName, final String title) {
      this.fxmlFileName = fxmlFileName;
      this.title = Config.getResourceText(title);
    }

    /**
     * Returns the Fxml File Name
     *
     * @return fxmlFileName
     */
    public String getFxmlFileName() {
      return fxmlFileName;
    }

    /**
     * Returns the title
     *
     * @return title
     */
    public String getTitel() {
      return title;
    }

  }
}
