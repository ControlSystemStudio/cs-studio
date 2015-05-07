/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.ui.resources.alarms.AlarmIcons;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** Helper that provides icon for severity levels
 *  @author Kay Kasemir
 */
public class SeverityIconProvider
{
    /** Pixel widths/height of icon */
    final private static int ICON_SIZE = 12;
    final private static int IMAGE_SIZE = 16;

    /** Icon to use for disabled items */
    final private Image disabled;
    
    /** Icons to use for the various severity levels.
     *  Index 0: Alarm severity
     *  Index 1: Current alarm severity
     *  Index 2: Anything disabled? */
    final private Image icons[][][];

    /** Initialize
     *  @param parent Parent widget
     */
    public SeverityIconProvider(final Composite parent)
    {
        final Display display = parent.getDisplay();
        icons = createIcons(display);
        disabled = AlarmIcons.getInstance().getDisabled().createImage(display);
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(final DisposeEvent e)
            {
                final int N = icons.length;
                for (int c=0; c<N; ++c)
                    for (int s=0; s<N; ++s)
                        for (int d=0; d<2; ++d)
                            icons[c][s][d].dispose();
                disabled.dispose();
            }
        });
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
     *  @param anything_disabled Well, is there?
     *  @return Icon for those severities
     */
    public Image getIcon(final SeverityLevel current_severity,
            final SeverityLevel severity, final boolean anything_disabled)
    {
        return icons[current_severity.ordinal()][severity.ordinal()][anything_disabled ? 1 : 0];
    }
    
    /** @return Array of icons */
    private static Image[][][] createIcons(final Display display)
    {
        final SeverityLevel[] severities = SeverityLevel.values();
        final Image icons[][][] = new Image[severities.length][severities.length][2];
        for (int c = 0; c < severities.length; c++)
        {
            for (int s = 0; s < severities.length; s++)
            {
                ImageDescriptor desc = getIconDescriptor(severities[c],severities[s],false);
                icons[c][s][0] = desc == null ? createOKIcon(display) : desc.createImage(display);
                desc = getIconDescriptor(severities[c],severities[s],true);
                icons[c][s][1] = desc == null ? createOKIcon(display) : desc.createImage(display);
            }
        }
        return icons;
    }

    /** Obtain icon suitable for displaying a severity level
     *  @param current_severity Current severity
     *  @param severity Latched SeverityLevel
     *  @param anything_disabled Well, is there?
     *  @return Icon for those severities
     */
    private static ImageDescriptor getIconDescriptor(final SeverityLevel current_severity, final SeverityLevel severity, 
            final boolean disabled)
    {
        final AlarmIcons icons = AlarmIcons.getInstance();
        switch(severity)
        {
            case UNDEFINED_ACK: 
            case INVALID_ACK:
                return icons.getInvalidAcknowledged(disabled);
            case UNDEFINED: 
            case INVALID:
                return current_severity == SeverityLevel.OK ?
                       icons.getInvalidClearedNotAcknowledged(disabled) : icons.getInvalidNotAcknowledged(disabled); 
            case MAJOR:
                return current_severity == SeverityLevel.OK ?
                       icons.getMajorClearedNotAcknowledged(disabled) : icons.getMajorNotAcknowledged(disabled);
            case MAJOR_ACK:
                return icons.getMajorAcknowledged(disabled);
            case MINOR:
                return current_severity == SeverityLevel.OK ? 
                       icons.getMinorClearedNotAcknowledged(disabled) : icons.getMinorNotAcknowledged(disabled);
            case MINOR_ACK:
                return icons.getMinorAcknowledged(disabled); 
            case OK: 
            default:
                return null;
        }
    }    
    
    /** @return the icon for the OK severity */
    private static Image createOKIcon(final Display display)
    {
        final SeverityLevel severity = SeverityLevel.OK;
        final Color color = new Color(
                severity.getRed(),
                severity.getGreen(),
                severity.getBlue());
        final BufferedImage awtImage = new BufferedImage(IMAGE_SIZE,
                IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
        final Graphics g = awtImage.getGraphics();
        g.setColor(new Color(255,255,255));
        g.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

        g.setColor(color);
        g.fillOval((IMAGE_SIZE-ICON_SIZE)/2, (IMAGE_SIZE - ICON_SIZE)/2, ICON_SIZE, ICON_SIZE);
        g.dispose();
        
        ImageData data = AWT2SWTImageConverter.convertToSWT(awtImage);
        int whitePixel = data.palette.getPixel(new RGB(255,255,255));
        data.transparentPixel = whitePixel;
        return new Image(display, data);
    }
}
