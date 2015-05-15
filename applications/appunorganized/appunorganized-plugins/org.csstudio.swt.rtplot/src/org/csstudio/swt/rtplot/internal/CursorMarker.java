/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.List;

import org.csstudio.swt.rtplot.SWTMediaPool;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

/** Mark where a trace crosses the cursor.
 *
 *  <p>Markers sort by y-position, and are painted in order
 *  with a certain gap between them.
 *
 *  @author Davy Dequidt - Original org.csstudio.swt.xygraph.figures.HoverLabels
 *  @author Kay Kasemir
 */
class CursorMarker implements Comparable<CursorMarker>
{
    /** Border around the marker's text */
    final private static int BORDER = 3;

    /** Size of the 'arrow' from point to text */
    final private static int ARROW = 20;

    /** Number of markers to shuffle to avoid overlap */
    final private static int MAX_SHUFFLE = 2;

    final private int x, y;
    final private RGB rgb;
    final private String label;

    /** @param x Pixel position
     *  @param y Pixel position
     *  @param rgb Color
     *  @param label Label
     */
    public CursorMarker(final int x, final int y, final RGB rgb, final String label)
    {
        this.x = x;
        this.y = y;
        this.rgb = rgb;
        this.label = label;
    }

    // Comparable
    @Override
    public int compareTo(final CursorMarker other)
    {
        return Integer.compare(y, other.y);
    }

    /** @param gc GC
     *  @param media
     *  @param markers {@link CursorMarker}s to draw
     *  @param bounds
     */
    public static void drawMarkers(final GC gc, final SWTMediaPool media, final List<CursorMarker> markers, final Rectangle bounds)
    {
        final int height = gc.getFontMetrics().getHeight() + 2*BORDER;
        int last_y = -1;
        int moved = 0;
        for (CursorMarker mark : markers)
        {
            // 'y' of markers is sorted low .. high
            int y = mark.y;
            // If marker overlaps last one, try to move it down, but not too often
            if (last_y >= 0  &&  last_y + height >= y)
            {
                if (++moved <= MAX_SHUFFLE)
                    y = last_y + height;
                else
                    continue;
            }
            else // At least one fit without shuffle, reset 'moved' count
                moved = 0;
            drawMark(gc, media, y, mark, bounds);
            last_y = y;
        }
    }

    private static void drawMark(final GC gc, final SWTMediaPool media, final int y, final CursorMarker mark, final Rectangle bounds)
    {
        final Color orig_color = gc.getForeground();
        gc.setForeground(media.get(mark.rgb));
        gc.setAlpha(180);
        final Point extent = gc.textExtent(mark.label);
        final int dir = (mark.x + ARROW + extent.x + BORDER <= bounds.width) ? 1 : -1;
        final int[] outline = new int[]
        {
            mark.x, mark.y,
            mark.x + dir * ARROW,                      y - extent.y/2 - BORDER,
            mark.x + dir *(ARROW + extent.x + BORDER), y - extent.y/2 - BORDER,
            mark.x + dir *(ARROW + extent.x + BORDER), y + extent.y/2 + BORDER,
            mark.x + dir * ARROW,                      y + extent.y/2 + BORDER,
        };
        gc.fillPolygon(outline);
        gc.drawPolygon(outline);
        gc.setAlpha(255);
        if (dir > 0)
            gc.drawText(mark.label, mark.x + ARROW, y - extent.y/2, true);
        else
            gc.drawText(mark.label, mark.x - ARROW - extent.x, y - extent.y/2, true);
        gc.setForeground(orig_color);
    }
}