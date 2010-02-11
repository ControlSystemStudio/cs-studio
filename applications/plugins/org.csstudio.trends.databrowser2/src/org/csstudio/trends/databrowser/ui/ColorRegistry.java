package org.csstudio.trends.databrowser.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/** Color Registry
 *  <p>
 *  Linked to a Control, this registry maps {@link RGB} values to {@link Color},
 *  disposing them when the control is disposed.
 * @author Kay Kasemir
 */
public class ColorRegistry implements DisposeListener
{
    final private Display display;
    
    /** Map from RGB to Color */
    final private Map<RGB, Color> colormap = new HashMap<RGB, Color>();

    /** Initialize
     *  @param control Control with which this registry will be disposed
     */
    public ColorRegistry(final Control control)
    {
        display = control.getDisplay();
        control.addDisposeListener(this);
    }

    /** DisposeListener */
    public void widgetDisposed(final DisposeEvent e)
    {
        final Iterator<Color> iter = colormap.values().iterator();
        while (iter.hasNext())
            iter.next().dispose();
        colormap.clear();
    }

    /** Turn RGB into a color
     *  @param rgb {@link RGB} description of the color
     *  @return SWT Color object. Do not dispose!
     */
    public Color getColor(final RGB rgb)
    {
        // Thought about using the ColorRegistry, but it maps
        // names to colors. Use the trace/item _name_?
        // No, because the color of an item can change,
        // plus using the RGB avoids duplicate entries
        // when the same color is re-used by different
        // traces.
        Color color = colormap.get(rgb);
        if (color == null)
        {   // As of yet unused color, need to create a new one
            color = new Color(display, rgb);
            colormap.put(rgb, color);
        }
        return color;
    }
}
