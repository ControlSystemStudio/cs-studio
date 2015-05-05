/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.opiwidget;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.swt.rtplot.RTTimePlot;
import org.csstudio.trends.databrowser2.ui.ModelBasedPlot;
import org.eclipse.swt.widgets.Composite;

/** OPI Figure that displays data browser plot on screen,
 *  holds a Data Browser Plot
 *
 *  @author Kay Kasemir
 */
public class DataBrowserWidgetFigure extends AbstractSWTWidgetFigure<RTTimePlot>
{
    /** Data Browser plot */
    private ModelBasedPlot plot;

    /** Initialize
     *  @param filename Configuration file name
     *  @param toolbar
     */
    public DataBrowserWidgetFigure(final AbstractBaseEditPart editPart, final boolean toolbar,
    		final String selectionValuePv, final boolean showValueLabels)
    {
        super(editPart);

        plot.getPlot().showToolbar(toolbar);
        plot.getPlot().showCrosshair(showValueLabels);
    }

    @Override
    protected RTTimePlot createSWTWidget(final Composite parent, final int style)
    {
        plot = new ModelBasedPlot(parent);
        return plot.getPlot();
    }

    /** @return Data Browser Plot */
    public ModelBasedPlot getDataBrowserPlot()
    {
        return plot;
    }

    /** @return Tool bar visibility */
    public boolean isToolbarVisible()
    {
        return plot.getPlot().isToolbarVisible();
    }

    /** @param visible New tool bar visibility */
    public void setToolbarVisible(final boolean visible)
    {
        plot.getPlot().showToolbar(visible);
    }

	/** @param showValueLabels <code>true</code> if values should be visible */
	public void setShowValueLabels(final boolean showValueLabels)
	{
        plot.getPlot().showCrosshair(showValueLabels);
	}
}