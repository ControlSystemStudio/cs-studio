package gov.bnl.shiftClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
*@author:eschuhmacher
 */
public class ShiftProperties {
    private static Preferences preferences;
    private static Properties defaultProperties;
    private static Properties userCFProperties;
    private static Properties userHomeCFProperties;
    private static Properties systemCFProperties;

    ShiftProperties(){

        preferences = Preferences.userNodeForPackage(ShiftClient.class);

        try {
            File userCFPropertiesFile = new File(System.getProperty(
                    "shift.properties", ""));
            File userHomeCFPropertiesFile = new File(System
                    .getProperty("user.home")
                    + "/shift.properties");
            File systemCFPropertiesFile = null;
            if (System.getProperty("os.name").startsWith("Windows")) {
                systemCFPropertiesFile = new File("/shift.properties");
            } else if (System.getProperty("os.name").startsWith("Linux")) {
                systemCFPropertiesFile = new File(
                        "/etc/shift.properties");
            } else {
                systemCFPropertiesFile = new File(
                        "/etc/shift.properties");
            }

            defaultProperties = new Properties();
            try {
                defaultProperties.load(this.getClass().getResourceAsStream(
                        "/config/shift.properties"));
            } catch (Exception e) {
            }


            systemCFProperties = new Properties(defaultProperties);
            if (systemCFPropertiesFile.exists()) {
                systemCFProperties.load(new FileInputStream(
                        systemCFPropertiesFile));
            }
            userHomeCFProperties = new Properties(systemCFProperties);
            if (userHomeCFPropertiesFile.exists()) {
                userHomeCFProperties.load(new FileInputStream(
                        userHomeCFPropertiesFile));
            }
            userCFProperties = new Properties(userHomeCFProperties);
            if (userCFPropertiesFile.exists()) {
                userCFProperties
                        .load(new FileInputStream(userCFPropertiesFile));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * check java preferences for the requested key - then checks the various
     * default logbooks files.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    String getPreferenceValue(String key, String defaultValue) {
        return preferences.get(key, getDefaultValue(key, defaultValue));
    }

    /**
     * cycles through the default logbooks files and return the value for the
     * key from the highest priority file
     *
     * @param key
     * @param defaultValue
     * @return
     */
    private static String getDefaultValue(String key, String defaultValue) {
        if (userCFProperties.containsKey(key))
            return userCFProperties.getProperty(key);
        else if (userHomeCFProperties.containsKey(key))
            return userHomeCFProperties.getProperty(key);
        else if (systemCFProperties.containsKey(key))
            return systemCFProperties.getProperty(key);
        else if (defaultProperties.containsKey(key))
            return defaultProperties.getProperty(key);
        else
            return defaultValue;
    }

}
