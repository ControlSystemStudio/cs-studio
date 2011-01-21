/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.opiwidget;

import org.csstudio.trends.databrowser.ui.Plot;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/** OPI Figure that displays data browser plot on screen,
 *  holds a Data Browser Plot
 *
 *  @author Kay Kasemir
 */
public class DataBrowserWidgetFigure extends Figure
{
    /** Name of configuration file */
    private String filename;

    /** Data Browser plot */
    final private Plot plot;

    /** Initialize
     *  @param filename Configuration file name
     */
    public DataBrowserWidgetFigure(final String filename)
    {
        this.filename = filename;

        plot = Plot.forDraw2D();
        plot.setToolbarVisible(false);
        add(plot.getFigure());
    }

    /** @return Data Browser Plot */
    public Plot getDataBrowserPlot()
    {
        return plot;
    }

    /** @param filename New file name */
    public void setFilename(final String filename)
    {
        this.filename = filename;
        revalidate();
        repaint();
    }

    @Override
    protected void layout()
    {
        // Plot fills the figure
        plot.getFigure().setBounds(getClientArea());
        // Not using any LayoutManager
        // super.layout();
    }

    @Override
    protected void paintClientArea(final Graphics graphics)
    {
        // Paint children, i.e. plot
        super.paintClientArea(graphics);

        if (filename == null)
            return;

        // Display filename on top of figure, centered
        final Rectangle client = getClientArea();
        final Dimension text = TextUtilities.INSTANCE.getStringExtents(filename, getFont());
        final Rectangle rect =
            new Rectangle(client.x + (client.width - text.width)/2,
                          client.y + (client.height - text.height)/2,
                          text.width, text.height);
        graphics.fillRectangle(rect);
        graphics.drawString(filename, rect.x, rect.y);
    }
}
