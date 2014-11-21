/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import org.csstudio.swt.rtplot.Annotation;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/** Annotation that's displayed on a YAxis
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
public class AnnotationImpl<XTYPE extends Comparable<XTYPE>> extends Annotation<XTYPE>
{
    private boolean selected = false;

    /** The screen coordinates.
     *  Only updated when painted !
     */
    private Rectangle screen_pos = null;

    /** Constructor */
    public AnnotationImpl(final Trace<XTYPE> trace, final XTYPE position, final double value, final String text)
    {
        super(trace, position, value, text);
    }

    /** Set to new location
     *  @param position
     *  @param value
     */
    public void setLocation(final XTYPE position, final double value)
    {
        this.position = position;
        this.value = value;
    }

    /** @param text New annotation text, may include '\n' */
    public void setText(final String text)
    {
        this.text = text;
    }

    /** @return <code>true</code> if currently selected */
    public boolean isSelected()
    {
        return selected;
    }

    /** Set the selection state */
    void select(boolean selected)
    {
        this.selected = selected;
    }

    /** @return On-screen coordinates or <code>null</code> if never displayed. */
    Rectangle getScreenCoords()
    {
        return screen_pos;
    }

    /** 'X' marks the spot, and this is it's radius. */
    final private static int X_RADIUS = 2;

    /** Paint the annotation on given gc and axes. */
    void paint(final GC gc, final SWTMediaPool media, final AxisPart<XTYPE> xaxis, final YAxisImpl<XTYPE> yaxis)
    {
        // Somewhat like this:
        //
        //    Text
        //    Blabla
        //    Yaddi yaddi
        //    ___________
        //   /
        //  O
        final int x = xaxis.getScreenCoord(position);
        final int y = Double.isFinite(value) ? yaxis.getScreenCoord(value) : yaxis.getScreenRange().getLow();

        final String label = NLS.bind(text,
                new Object[]
                {
                    trace.getName(),
                    xaxis.getTicks().format(position),
                    yaxis.getTicks().format(value)
                });

        final Color o_col = gc.getForeground();
        final Point text_size = gc.textExtent(label, SWT.DRAW_DELIMITER);
        final int label_dist = gc.getAdvanceWidth('X');
        final int tx = x+label_dist, ty = y-label_dist;
        // Marker 'O' around the actual x/y point
        gc.drawOval(x-X_RADIUS, y-X_RADIUS, 2*X_RADIUS, 2*X_RADIUS);
        // '/'
        gc.drawLine(x+X_RADIUS, y-X_RADIUS, tx, ty);
        // Text
        final int txt_top = ty-text_size.y;
        // Update the screen position so that we can later 'select' this annotation.
        screen_pos = new Rectangle(tx, txt_top, text_size.x, text_size.y);
        gc.setForeground(media.get(trace.getColor()));
        gc.setAlpha(170);
        gc.fillRectangle(screen_pos);
        gc.setAlpha(255);
        gc.drawText(label, tx, txt_top, SWT.DRAW_DELIMITER | SWT.DRAW_TRANSPARENT);
        gc.setForeground(o_col);
        if (selected)
            gc.drawRectangle(screen_pos);
        else // '___________'
            gc.drawLine(tx, ty, tx+text_size.x, ty);
    }
}
