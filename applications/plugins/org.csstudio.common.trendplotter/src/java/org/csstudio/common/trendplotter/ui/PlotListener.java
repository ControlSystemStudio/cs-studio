/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.ui;

import org.csstudio.common.trendplotter.model.ArchiveDataSource;
import org.csstudio.common.trendplotter.model.AxisConfig;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.eclipse.swt.graphics.Color;

/** Interface used by Plot to send events in response to user input:
 *  Zoom changed, scrolling turned on/off
 *  @author Kay Kasemir
 *
 *  Add events necessary in response of GRAPH settings changed by user
 *  ADD events link to add/remove annotation
 *  @author Laurent PHILIPPE (GANIL)
 */
public interface PlotListener
{
    /** Called when the user enables/disables scrolling
     *  @param enable_scrolling true when user requested scrolling via GUI
     */
    public void scrollRequested(boolean enable_scrolling);

    /** Called when the user requests time config dialog. */
    public void timeConfigRequested();

    /** Called when the user enables/disables scrolling
     *  @param start_ms New time axis start time in ms since 1970
     *  @param end_ms ... end time ...
     */
    public void timeAxisChanged(long start_ms, long end_ms);

    /** Called when the user changed a value (Y) axis
     *  @param index Value axis index 0, 1, ...
     *  @param lower Lower range limit
     *  @param upper Upper range limit
     */
    public void valueAxisChanged(int index, double lower, double upper);

    /** Received a name, presumably a PV name via drag & drop
     *  @param name PV(?) name
     */
    public void droppedName(String name);

    /** Received a PV name and/or archive data source via drag & drop
     *  @param name PV name or <code>null</code>
     *  @param archive Archive data source or <code>null</code>
     */
    public void droppedPVName(ProcessVariable name, ArchiveDataSource archive);

    /** Received a file name */
    public void droppedFilename(String file_name);

    /**
     * Called when the user changed graph settings
     * @param newValue
     *            The new graph settings
     */
    public void xyGraphConfigChanged(XYGraph newValue);

    /**
     * Called when the user remove an annotation
     * @param oldValue
     *            The annotation removed
     */
    public void removeAnnotationChanged(Annotation oldValue);

    /**
     * Called when the user add an annotation
     * @param newValue
     *            The annotation added
     */
    public void addAnnotationChanged(Annotation newValue);

    /**
     * Called when the user changed the plot background color
     *
     * @param newValue
     *            New background color
     */
    public void backgroundColorChanged(Color newValue);

    /**
     * Called when the user changed time axis foreground color
     *
     * @param oldColor
     *            Old foreground color
     * @param newColor
     *            New foreground color
     */
    public void timeAxisForegroundColorChanged(Color oldColor, Color newColor);

    /**
     * Called when the user changed value axis foreground color
     *
     * @param index
     *            Value axis index 0, 1, ...
     * @param oldColor
     *            Old foreground color
     * @param newColor
     *            New foreground color
     */
    public void valueAxisForegroundColorChanged(int index, Color oldColor,
            Color newColor);

    /**
     * Called when the user changed value axis title
     *
     * @param index
     *            Value axis index 0, 1, ...
     * @param oldTitle
     *            Old title
     * @param newTitle
     *            New title
     */
    public void valueAxisTitleChanged(int index, String oldTitle,
            String newTitle);

    /**
     * Called when the user changed value axis autoscale mode
     *
     * @param index
     *            Value axis index 0, 1, ...
     * @param oldAutoScale
     *            Old autoscale mode
     * @param newAutoScale
     *            New autoscale mode
     */
    public void valueAxisAutoScaleChanged(int index, boolean oldAutoScale,
            boolean newAutoScale);



    /**
     * Called when the user changed trace display name
     * @param index     trace index 0, 1, ...
     * @param oldName   Old name
     * @param newName   New name
     */
    public void traceNameChanged(int index, String oldName, String newName);

    /**
     * Called when the user changed trace YAxis
     * @param index     trace index 0, 1, ...
     * @param oldConfig old trace config
     * @param config    new trace config
     */
    public void traceYAxisChanged(int index, AxisConfig oldConfig, AxisConfig config);


    /**
     * Called when the user changed trace type
     * @param index     trace index 0, 1, ...
     * @param old   old trace type
     * @param newTraceType  new trace type
     */
    public void traceTypeChanged(int index, TraceType old,
            TraceType newTraceType);

    /**
     * Called when the user changed trace color
     * @param index     trace index 0, 1, ...
     * @param old   old trace color
     * @param newColor  new trace color
     */
    public void traceColorChanged(int index, Color old, Color newColor);

    public void valueAxisLogScaleChanged(int index, boolean old,
            boolean logScale);
}
