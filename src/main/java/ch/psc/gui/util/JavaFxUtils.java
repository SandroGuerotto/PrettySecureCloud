package ch.psc.gui.util;

import ch.psc.gui.ControlledScreen;
import ch.psc.gui.SignUpController;
import ch.psc.gui.StartseiteController;
import ch.psc.presentation.Config;

/**
 * Contains help methods for JavaFx
 *
 * @author sevimrid
 */
public class JavaFxUtils {

    /**
     * No instance necessary
     */
    private JavaFxUtils(){
    }

    /**
     * Enum with the information about the controlled screens
     */
    public enum RegisteredScreen {
        LOGIN_PAGE("Startseite.fxml", "startseite.titel", StartseiteController.class),
        //TODo sevimrid , bananasprout, SandroGuerotto, ChrisWals
        REGISTER_PAGE("register.fxml", "register.titel", SignUpController.class);
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

        public Class<?> getControllerClass(){
            return controllerClass;
        }
    }
}
