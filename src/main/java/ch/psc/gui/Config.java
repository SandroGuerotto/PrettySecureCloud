package ch.psc.gui;

import java.time.LocalDate;
import java.util.ResourceBundle;


/**
 * Configuration class for all pages
 *
 * @author sevimrid
 */
public class Config {
    public static final String TEXT_BUNDLE_NAME = "text";
    public static final String ENUM_BUNDLE_NAME = "enum";

    public static final String NUMMER_FORMAT = "%,.2f";

    public static final int MIN_HEIGHT = 500;
    public static final int MIN_WIDTH = 730;

    /**
     * No Instanz required
     */
    private Config() {
    }

    /**
     * Returns the string to a resource key for a text.
     *
     * @param key key for property
     * @return value of property
     */
    public static String getResourceText(String key) {
        return ResourceBundle.getBundle(TEXT_BUNDLE_NAME).getString(key);
    }

    /**
     * Returns the label to an enum from the enum.properties file
     *
     * @param e Enum whose inscription is to be obtained
     * @return inscription for the enum
     */
    public static String getEnumLabel(Enum<?> e){
        String key = e.getClass().getSimpleName() + "." + e.name();
        return ResourceBundle.getBundle(ENUM_BUNDLE_NAME).getString(key);
    }

    /**
     *  Returns the current date
     *
     * @return current date
     */
    public static LocalDate now(){
        return LocalDate.now();
    }

}
