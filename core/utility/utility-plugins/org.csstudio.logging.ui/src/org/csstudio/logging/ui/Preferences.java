package org.csstudio.logging.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.graphics.RGB;

/**
 * 
 * <code>Preferences</code> provides the color settings for the logging streams.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Preferences {

    private static final String PLUGIN_ID = "org.csstudio.logging.ui";
    
    private static final String COLOR_SEVERE = "color_severe";
    private static final String COLOR_WARNING = "color_warning";
    private static final String COLOR_INFO = "color_info";
    private static final String COLOR_BASIC = "color_basic";

    private static final String SEVERE_DEFAULT = "255,0,255";
    private static final String WARNING_DEFAULT = "255,0,0";
    private static final String INFO_DEFAULT = "0,0,255";
    private static final String BASIC_DEFAULT = "0,0,0";

    /**
     * @return the RGB for the severe/error stream
     */
    public static RGB getColorSevere() {
        final IPreferencesService prefs = Platform.getPreferencesService();
        String text = SEVERE_DEFAULT;
        if (prefs != null) {
            text = prefs.getString(PLUGIN_ID, COLOR_SEVERE, SEVERE_DEFAULT, null);
        }
        return toRGB(text);
    }

    /**
     * @return the RGB for the warning stream
     */
    public static RGB getColorWarning() {
        final IPreferencesService prefs = Platform.getPreferencesService();
        String text = WARNING_DEFAULT;
        if (prefs != null) {
            text = prefs.getString(PLUGIN_ID, COLOR_WARNING, WARNING_DEFAULT, null);
        }
        return toRGB(text);
    }

    /**
     * @return the RGB for the info stream
     */
    public static RGB getColorInfo() {
        final IPreferencesService prefs = Platform.getPreferencesService();
        String text = INFO_DEFAULT;
        if (prefs != null) {
            text = prefs.getString(PLUGIN_ID, COLOR_INFO, INFO_DEFAULT, null);
        }
        return toRGB(text);
    }

    /**
     * @return the RGB for the default stream
     */
    public static RGB getColorBasic() {
        final IPreferencesService prefs = Platform.getPreferencesService();
        String text = BASIC_DEFAULT;
        if (prefs != null) {
            text = prefs.getString(PLUGIN_ID, COLOR_BASIC, BASIC_DEFAULT, null);
        }
        return toRGB(text);
    }

    private static RGB toRGB(String text) {
        if (text == null) {
            return new RGB(0, 0, 0);
        }
        String[] rgb = text.split("\\,");
        if (rgb.length == 3) {
            return new RGB(Integer.parseInt(rgb[0]),Integer.parseInt(rgb[1]),Integer.parseInt(rgb[2]));
        } else {
            return new RGB(0,0,0);
        }
    }
}
