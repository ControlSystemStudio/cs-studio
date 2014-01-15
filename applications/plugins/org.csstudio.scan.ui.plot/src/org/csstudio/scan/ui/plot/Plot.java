/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.figures.XYGraphFlags;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

/** Plot of scan data
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Plot
{
    final private Display display;
    private ToolbarArmedXYGraph plot;
	private XYGraph xygraph;
	private String x_device;

    /** Initialize
     *  @param parent Parent composite
     */
    public Plot(final Composite parent)
    {
        display = parent.getDisplay();
        // Create plot
        plot = new ToolbarArmedXYGraph(new XYGraph(), XYGraphFlags.SEPARATE_ZOOM);
        xygraph = plot.getXYGraph();
        xygraph.setTransparent(false);
        xygraph.setShowLegend(false);

        xygraph.primaryXAxis.setTitle(Messages.Plot_DefaultXAxisLabel);
        xygraph.primaryXAxis.setAutoScale(true);
        xygraph.primaryYAxis.setTitle(Messages.Plot_DefaultYAxisLabel);
        xygraph.primaryYAxis.setAutoScale(true);
        plot.setShowToolbar(false);
        xygraph.setShowLegend(true);

        // Embed Draw2D plot figure in SWT Canvas
        parent.setLayout(new FillLayout());
        final Canvas canvas = new Canvas(parent, 0);
        final LightweightSystem lws = new LightweightSystem(canvas);
        lws.setContents(plot);

        final MenuManager manager = new MenuManager();
        manager.add(new ToggleToolbarAction(plot));
        final Menu menu = manager.createContextMenu(canvas);
        canvas.setMenu(menu);
    }

    Display getDisplay()
    {
        return display;
    }

    /** @param title Plot title */
    public void setTitle(final String title)
    {
        xygraph.setTitle(title);
    }

    /** @param data_provider Data to show in trace */
    public void setDataProviders(final PlotDataProvider... data_providers)
    {
        // Remove all traces
        int N = xygraph.getPlotArea().getTraceList().size();
        while (N > 0)
            xygraph.removeTrace(xygraph.getPlotArea().getTraceList().get(--N));

        // Nothing to show?
        if (data_providers.length <= 0)
        {
			String xTitle = x_device == null || x_device.isEmpty() ? Messages.Plot_DefaultXAxisLabel : x_device;
			xygraph.primaryXAxis.setTitle(xTitle);
            xygraph.primaryYAxis.setTitle(Messages.Plot_DefaultYAxisLabel);
            return;
        }

        // X axis shows x device name
        xygraph.primaryXAxis.setTitle(data_providers[0].getXDevice());
        // Y axis shows default when legend is enabled...
        if (xygraph.isShowLegend())
            xygraph.primaryYAxis.setTitle(Messages.Plot_DefaultYAxisLabel);
        else
        {   // ... otherwise: Trace device names
            final StringBuilder y_axis_label = new StringBuilder();
            for (PlotDataProvider data_provider : data_providers)
            {
                if (y_axis_label.length() > 0)
                    y_axis_label.append(", ");
                y_axis_label.append(data_provider.getYDevice());
            }
            xygraph.primaryYAxis.setTitle(y_axis_label.toString());
        }
        // Create traces
        for (int i = 0; i<data_providers.length; ++i)
        {
            final PlotDataProvider data = data_providers[i];
            final Trace trace = new Trace(data.getYDevice(),
                    xygraph.primaryXAxis, xygraph.primaryYAxis, data);
            trace.setTraceType(TraceType.SOLID_LINE);
            trace.setPointStyle(PointStyle.FILLED_SQUARE);
            trace.setPointSize(8);
            trace.setTraceColor(XYGraphMediaFactory.getInstance().getColor(
                    XYGraph.DEFAULT_TRACES_COLOR[i % XYGraph.DEFAULT_TRACES_COLOR.length]));
            xygraph.addTrace(trace);
        }
        xygraph.performAutoScale();
    }

	public void setXDevice(String x_device) {
		this.x_device = x_device;
	}

}
