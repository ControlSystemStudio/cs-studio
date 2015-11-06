package org.csstudio.alarm.beast.ui.alarmtable;

import org.csstudio.alarm.beast.SeverityLevel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
*
* <code>SeverityAltColorProvider</code> provides alternate colors to be paired with severity colors.
* Used together with SeverityColorProvider for the alarm table's message column for the foreground/background color.
*
* @author Boris Versic
*
*/
public class SeverityColorPairProvider implements DisposeListener
{
    /** Colors to be used as pairs for the configured severity colors.
     *  Will be used for the text of given severity (or background, if painted reversed).
     *  Can contain null values, denoting that the default color should be used for a given severity. */
    final private Color colors[];

    /** Initialize
     *  @param parent Parent widget; dispose listener is added to allow cleanup
     *  @throws Exception on error
     */
    public SeverityColorPairProvider(final Composite parent)
    {
        colors = new Color[SeverityLevel.values().length];
        parent.addDisposeListener(this);
        
        final String[] pref_colors = Preferences.getSeverityPairColors();
        if (pref_colors.length == 0) return;

        final Display display = parent.getDisplay();
        for (String pref : pref_colors) {
        	final String[] values = pref.split(",");
            if (values.length != 4) continue; // ignore incorrect setting
            try
            {
                final String severity = values[0].trim();
                final int red = Integer.parseInt(values[1].trim());
                final int green = Integer.parseInt(values[2].trim());
                final int blue = Integer.parseInt(values[3].trim());
                
                SeverityLevel level = SeverityLevel.parse(severity);
                colors[level.ordinal()] = new Color(display, red, green, blue);

                // INVALID color pair is also used for UNDEFINED
                if (level == SeverityLevel.INVALID)
                	colors[SeverityLevel.UNDEFINED.ordinal()] = new Color(display, red, green, blue);
                if (level == SeverityLevel.INVALID_ACK)
                	colors[SeverityLevel.UNDEFINED_ACK.ordinal()] = new Color(display, red, green, blue);
            }
            catch (NumberFormatException ex)
            {
                continue; // ignore incorrect setting
            }

        }

        // If the *_ACK severity color pairs weren't specified, the 
        // MAJOR, MINOR & INVALID color pair is used for them
        if (colors[SeverityLevel.MINOR_ACK.ordinal()] == null && colors[SeverityLevel.MINOR.ordinal()] != null)
        	colors[SeverityLevel.MINOR_ACK.ordinal()] = new Color(display, colors[SeverityLevel.MINOR.ordinal()].getRGB());
        if (colors[SeverityLevel.MAJOR_ACK.ordinal()] == null && colors[SeverityLevel.MAJOR.ordinal()] != null)
        	colors[SeverityLevel.MAJOR_ACK.ordinal()] = new Color(display, colors[SeverityLevel.MAJOR.ordinal()].getRGB());
        if (colors[SeverityLevel.INVALID_ACK.ordinal()] == null && colors[SeverityLevel.INVALID.ordinal()] != null)
        	colors[SeverityLevel.INVALID_ACK.ordinal()] = new Color(display, colors[SeverityLevel.INVALID.ordinal()].getRGB());
    }

    /** @see DisposeListener */
	@Override
	public void widgetDisposed(DisposeEvent e) {
        for (Color color : colors)
            if (color != null) color.dispose();
	}
	
    /** Obtain the color to be used as the color-pair for the given severity level, or null if default should be used.
     *  @param severity SeverityLevel
     *  @return Color for that level or null if default should be used 
     */
    public Color getColor(final SeverityLevel severity)
    {
        return colors[severity.ordinal()];
    }
}
