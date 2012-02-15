package org.dataportal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Loads config parameters from 'dataportal.properties'.
 * 
 * Note that changes in the properties file will need application reloading to take effect. 
 * 
 * @author Oscar Fonts
 */
public final class Config {
   
    private final static Properties PROPERTIES = new Properties();
    private static Logger logger = Logger.getLogger(Config.class);

    static {
        try {
            PROPERTIES.load(new BufferedInputStream(Config.class.getResourceAsStream("/dataportal.properties")));
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private Config() {
    }

    /**
     * Read a configuration property.
     * 
     * @param property the property name to read.
     * @return String the property value. Or an empty string if something went wrong.
     */
    public static String get(String property) {
        String value = PROPERTIES.getProperty(property);
        if (value == null) {
            logger.error("There is no '" + property + "' configuration property, or it couldn't be read.");
            return "";
        } else {
            return value;
        }
    }   
}
