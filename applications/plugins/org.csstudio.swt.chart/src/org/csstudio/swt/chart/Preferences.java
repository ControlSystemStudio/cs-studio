package org.csstudio.swt.chart;

import org.csstudio.swt.chart.axes.TracePainter;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Chart preference reader
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    /** @return Marker type
     *  @see TracePainter
     */
    public static int getMarkerType()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return 0;
        return service.getInt(Activator.ID, "marker_type", 0, null);
    }

    /** @return Marker size in pixel */
    public static int getMarkerSize()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return 10;
        return service.getInt(Activator.ID, "marker_size", 10, null);
    }

    /** @return Saturation of the original color used for the min/max area. 0...1 */
    public static float getAreaSaturation()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return 0.4f;
        return service.getFloat(Activator.ID, "area_color_saturation", 0.4f, null);
    }

    /** @return Show tool-tips on values in chart? */
    public static boolean getShowValueToolTips()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return true;
        return service.getBoolean(Activator.ID, "show_value_tool_tips", true, null);
    }
}
