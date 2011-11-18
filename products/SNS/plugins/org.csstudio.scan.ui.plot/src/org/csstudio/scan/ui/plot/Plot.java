/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.figures.XYGraphFlags;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/** Plot of scan data
 *  @author Kay Kasemir
 */
public class Plot
{
    private ToolbarArmedXYGraph plot;
    private XYGraph xygraph;

    /** Initialize
     *  @param parent Parent composite
     */
    public Plot(final Composite parent)
    {
        // Create plot
        plot = new ToolbarArmedXYGraph(new XYGraph(), XYGraphFlags.SEPARATE_ZOOM);
        xygraph = plot.getXYGraph();
        xygraph.setTransparent(false);
        
        xygraph.primaryXAxis.setTitle("Scan");
        xygraph.primaryYAxis.setTitle("Value");

        // Embed Draw2D plot figure in SWT Canvas
        parent.setLayout(new FillLayout());
        final Canvas canvas = new Canvas(parent, 0);
        final LightweightSystem lws = new LightweightSystem(canvas);
        lws.setContents(plot);
    }
    
    /** @param data Data to show in trace */
    public void addTrace(final IDataProvider data)
    {
        final Trace trace = new Trace("data",
                xygraph.primaryXAxis,
                xygraph.primaryYAxis,
                data);
        trace.setTraceType(TraceType.SOLID_LINE);
        trace.setPointStyle(PointStyle.FILLED_DIAMOND);
        trace.setPointSize(10);
        xygraph.addTrace(trace);
        xygraph.performAutoScale();
    }
}
