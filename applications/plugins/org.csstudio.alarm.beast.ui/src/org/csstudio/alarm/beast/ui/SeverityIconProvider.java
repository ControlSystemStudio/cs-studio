/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import org.csstudio.alarm.beast.SeverityLevel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** Helper that provides icon for severity levels
 *  @author Kay Kasemir
 */
public class SeverityIconProvider
{
    /** Pixel widths/height of icon */
    final private static int ICON_SIZE = 12;

    /** Arc for rounded corner. ARC_SIZE==ICON_SIZE results in circle */
    final private static int ARC_SIZE = 10;

    /** Icon to use for disabled items */
    final private Image disabled;

    /** Icons to use for the various severity levels */
    final private Image icons[][];

    /** Initialize
     *  @param parent Parent widget
     */
    public SeverityIconProvider(final Composite parent)
    {
        final Display display = parent.getDisplay();
        disabled = new Image(display, ICON_SIZE, ICON_SIZE);
        final GC gc = new GC(disabled);
        gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
        gc.fillRoundRectangle(0, 0, ICON_SIZE, ICON_SIZE, ARC_SIZE, ARC_SIZE);
        gc.dispose();

        icons = createIcons(display);
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(final DisposeEvent e)
            {
                final int N = icons.length;
                for (int c=0; c<N; ++c)
                    for (int s=0; s<N; ++s)
                        icons[c][s].dispose();
                disabled.dispose();
            }
        });
    }

    /** @return Array of icons */
    private Image[][] createIcons(final Display display)
    {
        final SeverityLevel[] severities = SeverityLevel.values();
        final Image icons[][] = new Image[severities.length][severities.length];
        for (int c = 0; c < severities.length; c++)
        {
            final Color c_col = new Color(display,
                    severities[c].getRed(),
                    severities[c].getGreen(),
                    severities[c].getBlue());
            for (int s = 0; s < severities.length; s++)
            {
                icons[c][s] = new Image(display, ICON_SIZE, ICON_SIZE);
                final Color s_col = new Color(display,
                        severities[s].getRed(),
                        severities[s].getGreen(),
                        severities[s].getBlue());
                final GC gc = new GC(icons[c][s]);

                // Left rectangle for 'latched', right for 'current' indicator
                gc.setBackground(s_col);
                gc.setClipping(0, 0, ICON_SIZE/2, ICON_SIZE);
                gc.fillRoundRectangle(0, 0, ICON_SIZE, ICON_SIZE, ARC_SIZE, ARC_SIZE);
                gc.setBackground(c_col);
                gc.setClipping(ICON_SIZE/2, 0, ICON_SIZE/2, ICON_SIZE);
                gc.fillRoundRectangle(0, 0, ICON_SIZE, ICON_SIZE, ARC_SIZE, ARC_SIZE);

                gc.dispose();
                s_col.dispose();
            }
            c_col.dispose();
        }
        return icons;
    }

    /** Obtain icon suitable for displaying a disabled item
     *  @return Icon for those severities
     */
    public Image getDisabledIcon()
    {
        return disabled;
    }

    /** Obtain icon suitable for displaying a severity level
     *  @param current_severity Current severity
     *  @param severity Latched SeverityLevel
     *  @return Icon for those severities
     */
    public Image getIcon(final SeverityLevel current_severity,
            final SeverityLevel severity)
    {
        return icons[current_severity.ordinal()][severity.ordinal()];
    }
}
