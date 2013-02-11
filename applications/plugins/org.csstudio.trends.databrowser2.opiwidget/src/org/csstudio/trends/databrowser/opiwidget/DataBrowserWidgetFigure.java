/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.opiwidget;

import org.csstudio.trends.databrowser2.ui.Plot;
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
     *  @param toolbar
     */
    public DataBrowserWidgetFigure(final String filename, final boolean toolbar)
    {
        this.filename = filename;

        plot = Plot.forDraw2D();
        plot.setToolbarVisible(toolbar);
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

    /** @return Tool bar visibility */
    public boolean isToolbarVisible()
    {
        return plot.isToolbarVisible();
    }

    /** @param visible New tool bar visibility */
    public void setToolbarVisible(final boolean visible)
    {
        plot.setToolbarVisible(visible);
    }

    /** Have plot fill the figure */
    @Override
    protected void layout()
    {
        plot.getFigure().setBounds(getClientArea());
        // Not using any LayoutManager
        // super.layout();
    }

    /** Display filename on top of plot */
    @Override
    protected void paintClientArea(final Graphics graphics)
    {
        // Paint children, i.e. plot
        super.paintClientArea(graphics);

        // Display file name or message
        final String text = (filename == null  ||  filename.isEmpty())
            ? Messages.NoFilename
            : filename;

        // Display filename on top of figure, centered
        final Rectangle client = getClientArea();
        final Dimension dim = TextUtilities.INSTANCE.getStringExtents(text, getFont());
        final Rectangle rect =
            new Rectangle(client.x + (client.width - dim.width)/2,
                          client.y + (client.height - dim.height)/2,
                          dim.width, dim.height);
        graphics.fillRectangle(rect);
        graphics.drawString(text, rect.x, rect.y);
    }
}
