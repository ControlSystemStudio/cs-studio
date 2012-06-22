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

        if (data_providers.length <= 0)
        {
            xygraph.primaryXAxis.setTitle(Messages.EmptyXTitle);
            xygraph.primaryYAxis.setTitle(Messages.EmptyYTitle);
        }
        else
        {
            xygraph.primaryXAxis.setTitle(data_providers[0].getXDevice());
            final StringBuilder y_axis_label = new StringBuilder();
            int traceNum = 0;
            for (PlotDataProvider data_provider : data_providers)
            {
                if (traceNum > 0)
                    y_axis_label.append(", ");
                y_axis_label.append(data_provider.getYDevice());

                final Trace trace = new Trace(data_provider.getYDevice(),
                        xygraph.primaryXAxis,
                        xygraph.primaryYAxis,
                        data_provider);
                trace.setTraceType(TraceType.SOLID_LINE);
                trace.setPointStyle(PointStyle.FILLED_DIAMOND);
                trace.setPointSize(10);
                trace.setTraceColor(XYGraphMediaFactory.getInstance().getColor(
                        XYGraph.DEFAULT_TRACES_COLOR[traceNum % XYGraph.DEFAULT_TRACES_COLOR.length]));
                xygraph.addTrace(trace);
                ++traceNum;
            }
            xygraph.primaryYAxis.setTitle(y_axis_label.toString());
            xygraph.performAutoScale();
        }
    }
}
