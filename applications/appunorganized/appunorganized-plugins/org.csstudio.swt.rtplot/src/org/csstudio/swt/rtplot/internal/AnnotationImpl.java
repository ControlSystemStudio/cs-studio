/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.Optional;

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
@SuppressWarnings("nls")
public class AnnotationImpl<XTYPE extends Comparable<XTYPE>> extends Annotation<XTYPE>
{
    /** 'X' marks the spot, and this is it's radius. */
    final private static int X_RADIUS = 4;

    /** What part of this annotation has been selected by the mouse? */
    public static enum Selection
    {   /** Nothing */
        None,
        /** The reference point, i.e. the location on the trace */
        Reference,
        /** The body of the annotation */
        Body
    };

    private Selection selected = Selection.None;

    /** Screen location of reference point, set when painted */
    private Optional<Point> screen_pos = Optional.empty();

    /** Screen location of annotation body, set when painted */
    private Optional<Rectangle> screen_box = Optional.empty();

    /** Constructor */
    public AnnotationImpl(final boolean internal, final Trace<XTYPE> trace, final XTYPE position, final double value, final Point offset, final String text)
    {
        super(internal, trace, position, value, offset, text);
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

    /** @param offset New offset from reference point to body of annotation */
    public void setOffset(final Point offset)
    {
        this.offset = offset;
    }

    /** @param text New annotation text, may include '\n' */
    public void setText(final String text)
    {
        this.text = text;
    }

    /** Check if the provided mouse location would select the annotation
     *  @param screen_location Location of mouse on screen
     *  @return <code>true</code> if this annotation gets selected at that mouse location
     */
    boolean isSelected(final Point screen_location)
    {
        final Optional<Rectangle> rect = screen_box;
        if (rect.isPresent()  &&  rect.get().contains(screen_location))
        {
            selected = Selection.Body;
            return true;
        }

        if (areWithinDistance(screen_pos, screen_location))
        {
            selected = Selection.Reference;
            return true;
        }

        return false;
    }

    /** @return Current selection state */
    Selection getSelection()
    {
        return selected;
    }

    void deselect()
    {
        selected = Selection.None;
    }

    private boolean areWithinDistance(final Optional<Point> pos, final Point pos2)
    {
        if (pos.isPresent())
        {
            final int dx = Math.abs(pos.get().x - pos2.x);
            final int dy = Math.abs(pos.get().y - pos2.y);
            return dx*dx + dy*dy <= X_RADIUS*X_RADIUS;
        }
        return false;
    }

    /** Paint the annotation on given gc and axes. */
    void paint(final GC gc, final SWTMediaPool media, final AxisPart<XTYPE> xaxis, final YAxisImpl<XTYPE> yaxis)
    {
        final int x = xaxis.getScreenCoord(position);
        final int y = Double.isFinite(value) ? yaxis.getScreenCoord(value) : yaxis.getScreenRange().getLow();
        screen_pos = Optional.of(new Point(x, y));

        String value_text = yaxis.getTicks().format(value);
        final String units = trace.getUnits();
        if (! units.isEmpty())
            value_text += " " + units;
        final String label = NLS.bind(text,
                new Object[]
                {
                    trace.getName(),
                    xaxis.getTicks().format(position),
                    value_text
                });

        // Layout like this:
        //
        //    Text
        //    Blabla
        //    Yaddi yaddi
        //    ___________
        //   /
        //  O
        final Point text_size = gc.textExtent(label, SWT.DRAW_DELIMITER);
        final int tx = x + offset.x, ty = y + offset.y;
        final int txt_top = ty-text_size.y;
        // Update the screen position so that we can later 'select' this annotation.
        final Rectangle rect = new Rectangle(tx, txt_top, text_size.x, text_size.y);
        screen_box = Optional.of(rect);

        // Marker 'O' around the actual x/y point, line to annotation.
        // Line first from actual point, will then paint the 'O' over it
        final int line_x = (x <= tx + text_size.x/2) ? tx : tx+text_size.x;
        final int line_y = (y > ty - text_size.y/2) ? ty : ty-text_size.y;
        gc.drawLine(x, y, line_x, line_y);

        // Fill with background (white), then draw around to get higher-contrast 'O'
        gc.fillOval(x-X_RADIUS, y-X_RADIUS, 2*X_RADIUS, 2*X_RADIUS);
        gc.drawOval(x-X_RADIUS, y-X_RADIUS, 2*X_RADIUS, 2*X_RADIUS);

        // Text
        final Color o_col = gc.getForeground();
        gc.setForeground(media.get(trace.getColor()));
        gc.setAlpha(170);
        gc.fillRectangle(rect);
        gc.setAlpha(255);
        gc.drawText(label, tx, txt_top, SWT.DRAW_DELIMITER | SWT.DRAW_TRANSPARENT);

        // Line over or under the text
        gc.setForeground(o_col);
        if (selected != Selection.None)
            gc.drawRectangle(rect);
        else // '___________'
            gc.drawLine(tx, line_y, tx+text_size.x, line_y);
    }
}
