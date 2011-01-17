/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.clock;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** Displays time on for example a 25 hour face.
 *  @author Kay Kasemir
 */
public class ClockWidget extends Canvas implements DisposeListener,
		PaintListener
{
	private static final int OVAL_WIDTH = 5;
	private Color background, face, pointer;
    /** The total hours in a day.
     *  24 gives the usual clock, but one can also use 25.
     */
    private final int hours;

	/** Constructor */
	public ClockWidget(int hours, Composite parent, int style)
	{
		super(parent, style);
        this.hours = hours;
		background = new Color(null, 255, 255, 255);
		face = new Color(null, 20, 10, 10);
		pointer = new Color(null, 200, 0, 0);
		addDisposeListener(this);
		addPaintListener(this);
	}

	/** @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean) */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed)
	{
		int width, height;
		height = 100;
		width = 100;
		if (wHint != SWT.DEFAULT)
			width = wHint;
		if (hHint != SWT.DEFAULT)
			height = hHint;
		return new Point(width, height);
	}

	/** @see org.eclipse.swt.events.DisposeListener */
	@Override
    public void widgetDisposed(DisposeEvent e)
	{
		pointer.dispose();
		face.dispose();
		background.dispose();
	}

	/** @see org.eclipse.swt.events.PaintListener */
    @Override
    public void paintControl(PaintEvent e)
    {
        GC gc = e.gc;
        Rectangle client_rect = getClientArea();

        // Determine the rectangle used by the clock face
        Rectangle r;
        int cx, cy; // center coordinates
        int diameter;
        if (client_rect.width < client_rect.height)
        {
            diameter = client_rect.width;
            r = new Rectangle(0, (client_rect.height - client_rect.width) / 2,
                    diameter, diameter);
            cx = diameter/2;
            cy = cx + r.y;
        }
        else
        {
            diameter = client_rect.height;
            r = new Rectangle((client_rect.width - client_rect.height) / 2, 0,
                    diameter, diameter);
            cy = diameter/2;
            cx = cy + r.x;
        }
        int radius = diameter / 2;

        // Clock face
        gc.setForeground(face);
        gc.setLineWidth(OVAL_WIDTH);
        r.x += OVAL_WIDTH;
        r.y += OVAL_WIDTH;
        r.width -= 2*OVAL_WIDTH;
        r.height -= 2*OVAL_WIDTH;
        radius -= OVAL_WIDTH;
        gc.setBackground(background);
        gc.fillOval(r.x, r.y, r.width, r.height);
        gc.drawOval(r.x, r.y, r.width, r.height);

        // Ticks
        gc.setLineWidth(3);
        gc.setLineCap(SWT.CAP_ROUND);
        FontMetrics fm = gc.getFontMetrics();

        int tick_radius = radius*9/10;
        int text_radius = tick_radius - 2 * fm.getAverageCharWidth();
        int pointer_radius = text_radius - fm.getAverageCharWidth() * 3/2;

        // Some horribly arbitrary criteria as to what to draw,
        // depending on the avaialble space.
        // Determined by trial and error, could be replaced
        // with an exact computation based on text size etc.
        int skip = 1;
        //System.out.println("Draw, radius = " + radius);
        if (radius < 100)
            skip = 2;
        if (radius < 60)
            skip = 3;
        if (radius < 45)
        {
            skip = 25;
            pointer_radius = tick_radius - 2;
        }
        // Draw major tick marks.
        for (int hour=0; hour<hours; hour += skip)
        {
            if (skip > 1 && hour == (hours - 1))
                break;
            // Angle for the hour in usual coords where
            // 0 deg == 'right, 90 deg == 'up', in radians:
            final double angle = Math.PI/2.0 - 2.0*Math.PI*hour/hours;
            // Cosine and sine for that angle, noting that GC's y axis goes 'down'
            final double c = Math.cos(angle);
            final double s = -Math.sin(angle);
            gc.drawLine(
                    cx + (int)(tick_radius*c),
                    cy + (int)(tick_radius*s),
                    cx + (int)(radius*c),
                    cy + (int)(radius*s));
            if (radius > 45)
            {
                final String text = Integer.toString(hour);
                gc.drawText(text,
                    cx + (int)(text_radius*c)
                    - (fm.getAverageCharWidth() * text.length())/2,
                    cy + (int)(text_radius*s)
                    - fm.getHeight()/2, true);
            }
        }

        // Draw the pointer
        gc.setForeground(pointer);
        Calendar now = Calendar.getInstance();
        // Get the usual 24h clock as 0 - 23.9999 hours
        double hour = now.get(Calendar.HOUR_OF_DAY)
            + now.get(Calendar.MINUTE) / 60.0
            + now.get(Calendar.SECOND) / 60.0 / 60.0;
        // Convert to e.g. 25hour day
        hour = hour * hours / 24.0;
        // Angle for the hour in usual coords where
        // 0 deg == 'right, 90 deg == 'up', in radians:
        final double angle = Math.PI/2.0 - 2.0*Math.PI*hour/hours;
        // Cosine and sine for that angle, noting that GC's y axis goes 'down'
        final double c = Math.cos(angle);
        final double s = -Math.sin(angle);
        gc.setLineWidth(5);
        gc.drawLine(cx, cy,
                cx + (int)(pointer_radius*c),
                cy + (int)(pointer_radius*s));

        // Schedule another redraw
        scheduleRedraw();
    }

	/** Start a UI timer for updating the clock widget. */
	private void scheduleRedraw()
	{
        // Everey 30 seconds? This could be smarter!
        int delay = 30;
		Display.getDefault().timerExec(1000 * delay, new Runnable()
		{
			@Override
            public void run()
			{
				// Mark for redraw.
				if (! isDisposed())
					redraw();
			}
		});
	}
}
