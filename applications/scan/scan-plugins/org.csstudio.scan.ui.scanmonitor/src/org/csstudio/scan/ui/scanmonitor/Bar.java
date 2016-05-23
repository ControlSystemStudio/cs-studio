/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scanmonitor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/** Bar display for memory usage
 *
 *  <p>Displays a text with underlying bar for 0..100 percentage indication.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Bar extends Canvas implements PaintListener
{
    final private static int WARNING_PERCENTAGE = 75;
    final private static int CRITICAL_PERCENTAGE = 90;
    private float percentage = 0.0f;
    private String text = "";
    private GC gc;

    /** Initialize
     *  @param parent
     *  @param style
     */
    public Bar(final Composite parent, final int style)
    {
        super(parent, style);
        addPaintListener(this);
    }

    /** Update values to show
     *  @param text Text to show
     *  @param percentage 0..100
     */
    public void update(final String text, final double percentage)
    {
        this.text = text;
        this.percentage = (float) percentage;
        redraw();
    }

    /** Adjust height to match height of text */
    @Override
    public Point computeSize(final int wHint, final int hHint, boolean changed)
    {
        final Point size = super.computeSize(wHint, hHint, changed);
        gc = new GC(getDisplay());
        size.y = gc.getFontMetrics().getHeight();
        gc.dispose();
        return size;
    }

    /** Custom painting */
    @Override
    public void paintControl(final PaintEvent e)
    {
        final GC gc = e.gc;
        final Rectangle area = getClientArea();

        // Bar
        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
        gc.fillRectangle(area);
        if (percentage > CRITICAL_PERCENTAGE)
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
        else if (percentage > WARNING_PERCENTAGE)
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_YELLOW));
        else
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
        gc.fillRectangle(0, 0, Math.round((area.width-1) * percentage / 100), area.height-1);

        // Using default foreground
        gc.drawRectangle(0, 0, area.width-1, area.height-1);

        // Text
        final Point size = gc.stringExtent(text);
        gc.drawText(text, (area.width - size.x)/2, (area.height - size.y)/2, true);
    }
}
