/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es.util;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.logging.es.Activator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Settings for a "Severity" table column, how to color-code them.
 * 
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SeverityColumnPreference implements DisposeListener
{
    /**
     * Name of the preference setting.
     * 
     * @see #DEFAULT_SETTING
     */
    private static final String SEVERITY_COLORS = "severity_colors";

    /** Default setting: ERROR is red, ... */
    private static final String DEFAULT_SETTING = "SEVERE,255,0,0|WARNING,255,255,0|INFO,255,255,255|CONFIG,255,255,255|FINE,255,255,255|FINER,255,255,255|FINEST,255,255,255";

    /** Mapping from a severity string to a color */
    private final Map<String, Color> color_map = new HashMap<>();

    /**
     * Constructor, reads severity color preferences.
     * 
     * @param parent
     *            Used to dispose colors via DisposeListener
     * @throws Exception
     *             on error <b>Note: The colors need to be disposed by the
     *             caller!
     * @see #SEVERITY_COLORS
     */
    public SeverityColumnPreference(final Composite parent) throws Exception
    {
        Display display = Display.getCurrent();
        String pref_text = DEFAULT_SETTING;
        // Try to read preferences
        IPreferencesService service = Platform.getPreferencesService();
        if (service != null)
        {
            pref_text = service.getString(Activator.ID, SEVERITY_COLORS,
                    pref_text, null);
        }
        // Split columns on '|'
        String[] colspecs = pref_text.split("\\|");
        for (String colspec : colspecs)
        { // Split into name, red, green, blue
            String[] pieces = colspec.split(",");
            if (pieces.length != 4)
            {
                throw new RuntimeException(
                        "Error in severity color preference '" + colspec + "'");
            }
            try
            {
                String name = pieces[0].trim();
                int red = Integer.parseInt(pieces[1].trim());
                int green = Integer.parseInt(pieces[2].trim());
                int blue = Integer.parseInt(pieces[3].trim());
                this.color_map.put(name, new Color(display, red, green, blue));
            }
            catch (NumberFormatException ex)
            {
                throw new RuntimeException(
                        "Cannot parse name, red, green, blue from '" + colspec
                                + "'");
            }
        }
        // We'll use this to dispose the colors that we just created
        parent.addDisposeListener(this);
    }

    /**
     * @param severity
     *            Name of a severity
     * @return Color for given severity, or <code>null</code>
     */
    public Color getColor(final String severity)
    {
        return this.color_map.get(severity);
    }

    /**
     * Dispose all colors.
     * 
     * @see DisposeListener
     */
    @Override
    public void widgetDisposed(final DisposeEvent e)
    {
        this.color_map.forEach((k, v) -> {
            v.dispose();
        });
    }
}
