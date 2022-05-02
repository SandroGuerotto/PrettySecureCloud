package ch.psc.gui.util;

import ch.psc.gui.Config;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.awt.*;
import java.net.URI;

/**
 * Contains help methods for JavaFx
 *
 * @author sevimrid, SandroGuerotto, bananasprout
 */
public class JavaFxUtils {

    /**
     * No instance necessary
     */
    private JavaFxUtils(){
    }

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
     * @param url the URL to be displayed in the user default browser
     */
    public static void openInBrowser(String url){
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enum with the information about the controlled screens
     */
    public enum RegisteredScreen {
        //TODo sevimrid , bananasprout, SandroGuerotto, ChrisWals
        LOGIN_PAGE("login.fxml", "login.title"),
        SIGNUP_PAGE("signUp.fxml", "signup.title"),
        FILEBROWSERPAGE("fileBrowser.fxml", "fileBrowser.titel");

//        CLOUDSERVICESPAGE("", ""),


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
