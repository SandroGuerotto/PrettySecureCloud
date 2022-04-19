package ch.psc.gui.util;

import ch.psc.gui.ControlledScreen;
import ch.psc.gui.LoginController;
import ch.psc.gui.SignUpController;
import ch.psc.presentation.Config;

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
        //LOGIN_PAGE("login.fxml", "login.titel", LoginController.class), // TODO @bananasprout
        SIGNUP_PAGE("signUp.fxml", "signup.titel", SignUpController.class),
        LOGIN_PAGE("login.fxml", "signup.titel", LoginController.class);
//        CLOUDSERVICESPAGE("", ""),
//        FILEBROWSERPAGE("", "");

        private final String fxmlFileName;
        private final String title;
        private final Class<? extends ControlledScreen> controllerClass;

        /**
         * Set fxmlFileName and title
         *
         * @param fxmlFileName Fxml file name of the controller
         * @param title Title of the window
         * @param clazz controller class type
         */
        RegisteredScreen(final String fxmlFileName, final String title, final Class<? extends ControlledScreen> clazz) {
            this.fxmlFileName = fxmlFileName;
            this.title = Config.getResourceText(title);
            controllerClass = clazz;
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

        /**
         * Returns controller Class
         * @return class type of controller
         */
        public Class<? extends ControlledScreen> getControllerClass(){
            return controllerClass;
        }
    }
}
