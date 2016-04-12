/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import org.csstudio.swt.rtplot.SWTMediaPool;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/** Part for plot title
 *  @author Kay Kasemir
 */
public class TitlePart extends PlotPart
{
    /** @param name Part name
     *  @param listener {@link PlotPartListener}, or <code>null</code> if set via <code>setListener</code>
     */
    public TitlePart(final String name, final PlotPartListener listener)
    {
        super(name, listener);
    }

    /** @param gc
     *  @param font
     *  @return Desired height in pixels
     */
    public int getDesiredHeight(final GC gc, final Font font)
    {
        final String text = getName();
        if (text.isEmpty())
            return 0;
        final Font orig_font = gc.getFont();
        gc.setFont(font);
        final Point size = gc.textExtent(text);
        gc.setFont(orig_font);
        return size.y;
    }

    /** {@inheritDoc} */
    public void paint(final GC gc, final SWTMediaPool media, final Font font)
    {
        super.paint(gc, media);

        final String text = getName();
        if (text.isEmpty())
            return;

        final Font orig_font = gc.getFont();
        gc.setFont(font);
        final Color old_fg = gc.getForeground();
        gc.setForeground(media.get(getColor()));

        final Rectangle bounds = getBounds();
        final Point size = gc.textExtent(text);

        final int tx = bounds.x + (bounds.width - size.x) / 2;
        final int ty = bounds.y + (bounds.height - size.y) / 2;
        gc.drawText(text, tx, ty, true);
        gc.setForeground(old_fg);
        gc.setFont(orig_font);
    }
}
