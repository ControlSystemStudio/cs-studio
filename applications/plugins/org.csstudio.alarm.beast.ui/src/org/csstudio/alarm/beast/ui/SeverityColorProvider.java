/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import org.csstudio.alarm.beast.SeverityLevel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** Helper that provides colors for severity levels
 *  @author Kay Kasemir
 */
public class SeverityColorProvider implements DisposeListener
{
    /** Colors to use for the various severity levels */
    final private Color colors[];

    /** Initialize
     *  @param parent Parent widget; dispose listener is added to allow cleanup
     */
    public SeverityColorProvider(final Composite parent)
    {
        final Display display = parent.getDisplay();
        final SeverityLevel[] severities = SeverityLevel.values();
        colors = new Color[severities.length];
        for (int i = 0; i < severities.length; i++)
            colors[i] = new Color(display,
                    severities[i].getRed(),
                    severities[i].getGreen(),
                    severities[i].getBlue());
        parent.addDisposeListener(this);
    }

    /** @see DisposeListener */
    @Override
    public void widgetDisposed(final DisposeEvent e)
    {
        for (Color color : colors)
            color.dispose();
    }

    /** Obtain color suitable for displaying a severity level
     *  @param severity SeverityLevel
     *  @return Color for that level
     */
    public Color getColor(final SeverityLevel severity)
    {
        return colors[severity.ordinal()];
    }
}
