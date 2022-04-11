package ch.psc.gui.util;

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
    public enum RegistrierterScreen {
        LOGINPAGE("Startseite.fxml", "startseite.titel");
        //TODo sevimrid , bananasprout, SandroGuerotto, ChrisWals
//        REGISTERPAGE("", ""),
//        CLOUDSERVICESPAGE("", ""),
//        FILEBROWSERPAGE("", "");

        private final String fxmlFileName;
        private final String title;

        /**
         * Set fxmlFileName and title
         *
         * @param fxmlFileName Fxml file name of the controller
         * @param title Title of the window
         */
        RegistrierterScreen(final String fxmlFileName, final String title) {
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
