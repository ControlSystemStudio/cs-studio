/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import java.util.HashMap;
import java.util.Iterator;

import org.csstudio.alarm.beast.msghist.Activator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** Settings for a "Severity" table column, how to color-code them.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SeverityColumnPreference implements DisposeListener
{
	/** Name of the preference setting.
	 *  @see #DEFAULT_SETTING
	 */
    private static final String SEVERITY_COLORS = "severity_colors";

	/** Default setting: ERROR is red, ... */
	private static final String DEFAULT_SETTING =
	    "INVALID,50,50,50|FATAL,255,0,10|ERROR,255,0,0|MAJOR,255,0,0|MINOR,255,255,0|WARN,255,255,0|INFO,130,130,255|NO_ALARM,0,255,0";

	/** Mapping from a severity string to a color */
	private final HashMap<String, Color> color_map = new HashMap<String, Color>();

	/** Constructor, reads severity color preferences.
	 *  @param parent Used to dispose colors via DisposeListener
     *  @throws Exception on error
     *  <b>Note: The colors need to be disposed by the caller!
     *  @see #SEVERITY_COLORS
     */
    public SeverityColumnPreference(final Composite parent) throws Exception
    {
    	final Display display = Display.getCurrent();
    	String pref_text = DEFAULT_SETTING;
        // Try to read preferences
        final IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
        	pref_text = service.getString(Activator.ID,
        									SEVERITY_COLORS, pref_text, null);
        // Split columns on '|'
        final String[] colspecs = pref_text.split("\\|");
        for (String colspec : colspecs)
        {   // Split into name, red, green, blue
            final String[] pieces = colspec.split(",");
            if (pieces.length != 4)
                throw new Exception("Error in severity color preference '" + colspec + "'");
            try
            {
                final String name = pieces[0].trim();
                final int red = Integer.parseInt(pieces[1].trim());
                final int green = Integer.parseInt(pieces[2].trim());
                final int blue = Integer.parseInt(pieces[3].trim());
				color_map.put(name, new Color(display, red, green, blue));
            }
            catch (NumberFormatException ex)
            {
                throw new Exception("Cannot parse name, red, green, blue from '" + colspec + "'");
            }
        }
        // We'll use this to dispose the colors that we just created
        parent.addDisposeListener(this);
    }

    /** @parm severity Name of a severity
     *  @return Color for given severity, or <code>null</code>
     */
    public Color getColor(final String severity)
    {
    	return color_map.get(severity);
    }

    /** Dispose all colors.
     *  @see DisposeListener
     */
	@Override
    public void widgetDisposed(final DisposeEvent e)
	{
		final Iterator<Color> colors = color_map.values().iterator();
		while (colors.hasNext())
			colors.next().dispose();
	}
}
