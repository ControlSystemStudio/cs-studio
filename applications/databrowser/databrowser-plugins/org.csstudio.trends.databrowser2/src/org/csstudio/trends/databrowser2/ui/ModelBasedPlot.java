/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.swt.rtplot.Annotation;
import org.csstudio.swt.rtplot.Axis;
import org.csstudio.swt.rtplot.AxisRange;
import org.csstudio.swt.rtplot.RTPlotListener;
import org.csstudio.swt.rtplot.RTTimePlot;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.YAxis;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AnnotationInfo;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.ChannelInfo;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;

/** Data Browser 'Plot' that displays the samples in a {@link Model}.
 *  <p>
 *  Links the underlying {@link RTTimePlot} to the {@link Model}.
 *
 *  @author Kay Kasemir
 *  @author Laurent PHILIPPE Modify addListener method to add property changed event capability
 */
@SuppressWarnings("nls")
public class ModelBasedPlot
{
    /** Plot Listener */
    private Optional<PlotListener> listener = Optional.empty();

    /** {@link Display} used by this plot */
    final private Display display;

    /** Plot widget/figure */
    final private RTTimePlot plot;

    final private Map<Trace<Instant>, ModelItem> items_by_trace = new ConcurrentHashMap<>();

    /** Initialize plot
     *  @param parent Parent widget
     */
    public ModelBasedPlot(final Composite parent)
    {
        this.display = parent.getDisplay();
        plot = new RTTimePlot(parent);

        plot.setOpacity(Preferences.getOpacity());

        final ToolItem time_config_button =
                plot.addToolItem(SWT.PUSH, Activator.getDefault().getImage("icons/time_range.png"), Messages.StartEndDialogTT);
        time_config_button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                listener.ifPresent((l) -> l.timeConfigRequested());
            }
        });

        // Configure axes
        final Axis<Instant> time_axis = plot.getXAxis();
        time_axis.setName(Messages.Plot_TimeAxisName);
        final YAxis<Instant> value_axis = plot.getYAxes().get(0);
        value_axis.setName(Messages.Plot_ValueAxisName);

        // Forward user changes to plot to model
        plot.addListener(new RTPlotListener<Instant>()
        {
            @Override
            public void changedXAxis(final Axis<Instant> x_axis)
            {
                final AxisRange<Instant> range = x_axis.getValueRange();
                listener.ifPresent((l) -> l.timeAxisChanged(plot.isScrolling(), range.getLow(), range.getHigh()));
            }

            @Override
            public void changedYAxis(final YAxis<Instant> y_axis)
            {
                final int index = plot.getYAxes().indexOf(y_axis);
                final AxisRange<Double> range = y_axis.getValueRange();
                listener.ifPresent((l) -> l.valueAxisChanged(index, range.getLow(), range.getHigh()));
            }

            @Override
            public void changedAnnotations()
            {
                final List<AnnotationInfo> annotations = new ArrayList<>();
                final List<Trace<Instant>> traces = new ArrayList<>();
                for (Trace<Instant> trace : plot.getTraces())
                    traces.add(trace);
                for (Annotation<Instant> annotation : plot.getAnnotations())
                {
                    final int item_index = traces.indexOf(annotation.getTrace());
                    annotations.add(new AnnotationInfo(annotation.isInternal(),
                                                       item_index,
                                                       annotation.getPosition(), annotation.getValue(),
                                                       annotation.getOffset(), annotation.getText()));
                }
                listener.ifPresent((l) -> l.changedAnnotations(annotations));
            }

            @Override
            public void changedCursors()
            {
                for (Trace<Instant> trace : plot.getTraces())
                    findModelItem(trace).setSelectedSample(trace.getSelectedSample());
                listener.ifPresent((l) -> l.selectedSamplesChanged());
            }

            @Override
            public void changedToolbar(final boolean visible)
            {
                listener.ifPresent((l) -> l.changedToolbar(visible));
            }

            @Override
            public void changedLegend(final boolean visible)
            {
                listener.ifPresent((l) -> l.changedLegend(visible));
            }

            @Override
            public void changedAutoScale(final YAxis<Instant> y_axis)
            {
                final int index = plot.getYAxes().indexOf(y_axis);
                listener.ifPresent((l) -> l.autoScaleChanged(index, y_axis.isAutoscale()));
            }
        });

        hookDragAndDrop(plot);
    }

    /** @return RTTimePlot */
    public RTTimePlot getPlot()
    {
        return plot;
    }

    /**
     * Attach to drag-and-drop, notifying the plot listener
     *
     * @param canvas
     */
    private void hookDragAndDrop(final Composite parent)
    {
        // Allow dropped arrays
        new ControlSystemDropTarget(parent, ChannelInfo[].class,
                ProcessVariable[].class, ArchiveDataSource[].class,
                File.class,
                String.class)
        {
            @Override
            public void handleDrop(final Object item)
            {
                final PlotListener lst = listener.orElse(null);
                if (lst == null)
                    return;

                if (item instanceof ChannelInfo[])
                {
                    final ChannelInfo[] channels = (ChannelInfo[]) item;
                    final int N = channels.length;
                    final ProcessVariable[] pvs = new ProcessVariable[N];
                    final ArchiveDataSource[] archives = new ArchiveDataSource[N];
                    for (int i=0; i<N; ++i)
                    {
                        pvs[i] = channels[i].getProcessVariable();
                        archives[i] = channels[i].getArchiveDataSource();
                    }
                    lst.droppedPVNames(pvs, archives);
                }
                else if (item instanceof ProcessVariable[])
                {
                    final ProcessVariable[] pvs = (ProcessVariable[]) item;
                    lst.droppedPVNames(pvs, null);
                }
                else if (item instanceof ArchiveDataSource[])
                {
                    final ArchiveDataSource[] archives = (ArchiveDataSource[]) item;
                    lst.droppedPVNames(null, archives);
                }
                else if (item instanceof String)
                {
                    final List<String> pvs = new ArrayList<>();
                    // Allow passing in many names, assuming that white space separates them
                    final String[] names = ((String)item).split("[\\r\\n\\t ]+"); //$NON-NLS-1$
                    for (String one_name : names)
                    {   // Might also have received "[pv1, pv2, pv2]", turn that into "pv1", "pv2", "pv3"
                        String suggestion = one_name;
                        if (suggestion.startsWith("["))
                            suggestion = suggestion.substring(1);
                        if (suggestion.endsWith("]")  &&  !suggestion.contains("["))
                            suggestion = suggestion.substring(0, suggestion.length()-1);
                        if (suggestion.endsWith(","))
                            suggestion = suggestion.substring(0, suggestion.length()-1);
                        pvs.add(suggestion);
                    }
                    if (pvs.size() > 0)
                        lst.droppedNames(pvs.toArray(new String[pvs.size()]));
                }
                else if (item instanceof String[])
                {   // File names arrive as String[]...
                    final String[] files = (String[])item;
                    try
                    {
                        for (String filename : files)
                            lst.droppedFilename(filename);
                    }
                    catch (Exception ex)
                    {
                        ExceptionDetailsErrorDialog.openError(parent.getShell(), Messages.Error, ex);
                    }
                }
            }
        };
    }

    /** Add a listener (currently only one supported) */
    public void addListener(final PlotListener listener)
    {
        if (this.listener.isPresent())
            throw new IllegalStateException("Only one listener supported");
        this.listener = Optional.of(listener);
    }

    /** Remove all axes and traces */
    public void removeAll()
    {
        items_by_trace.clear();
        // Remove all traces
        for (Trace<Instant> trace : plot.getTraces())
            plot.removeTrace(trace);
        // Now that Y axes are unused, remove all except for primary
        int N = plot.getYAxes().size();
        while (N > 1)
            plot.removeYAxis(--N);
    }

    /** @param index
     *             Index of Y axis. If it doesn't exist, it will be created.
     *  @return Y Axis
     */
    private YAxis<Instant> getYAxis(final int index)
    {
        // Get Y Axis, creating new ones if needed
        int N = plot.getYAxes().size();
        while (N <= index)
        {
            plot.addYAxis(NLS.bind(Messages.Plot_ValueAxisNameFMT, N));
            N = plot.getYAxes().size();
        }
        return plot.getYAxes().get(index);
    }

    /** Update value axis from model
     *  @param index Axis index. Y axes will be created as needed.
     *  @param config Desired axis configuration
     */
    public void updateAxis(final int index, final AxisConfig config)
    {
        final YAxis<Instant> axis = getYAxis(index);
        axis.setName(config.getResolvedName());
        axis.useAxisName(config.isUsingAxisName());
        axis.useTraceNames(config.isUsingTraceNames());
        axis.setColor(config.getColor());
        axis.setLabelFont(config.getLabelFont());
        axis.setScaleFont(config.getScaleFont());
        axis.setLogarithmic(config.isLogScale());
        axis.setGridVisible(config.isGridVisible());
        axis.setAutoscale(config.isAutoScale());
        axis.setValueRange(config.getMin(), config.getMax());
        axis.setVisible(config.isVisible());
        axis.setOnRight(config.isOnRight());
    }

    /** Add a trace to the plot
      *  @param item ModelItem for which to add a trace
     *  @author Laurent PHILIPPE
     */
    public void addTrace(final ModelItem item)
    {
        final Trace<Instant> trace = plot.addTrace(item.getResolvedDisplayName(),
                item.getUnits(),
                item.getSamples(),
                item.getColor(),
                item.getTraceType(), item.getLineWidth(),
                item.getPointType(), item.getPointSize(),
                item.getAxisIndex());
        items_by_trace.put(trace, item);
    }

    /** @param item ModelItem to remove from plot */
    public void removeTrace(final ModelItem item)
    {
        final Trace<Instant> trace;
        try
        {
            trace = findTrace(item);
        }
        catch (IllegalArgumentException ex)
        {   // Could be called with a trace that was not visible,
            // so it was never in the plot,
            // and now gets removed.
            // --> No error, because trace to be removed is already
            //     absent from plot
            return;
        }
        plot.removeTrace(trace);
        items_by_trace.remove(trace);
    }

    /** Update the configuration of a trace from Model Item
     *  @param item Item that was previously added to the Plot
     */
    public void updateTrace(final ModelItem item)
    {
        // Invisible items have no trace, nothing to update,
        // and findTrace() would throw an exception
        if (! item.isVisible())
            return;
        final Trace<Instant> trace = findTrace(item);
        // Update Trace with item's configuration
        final String name = item.getResolvedDisplayName();
        if (!trace.getName().equals(name))
            trace.setName(name);
        trace.setUnits(item.getUnits());
        // These happen to not cause an immediate redraw, so
        // set even if no change
        trace.setColor(item.getColor());
        trace.setType(item.getTraceType());
        trace.setWidth(item.getLineWidth());
        trace.setPointType(item.getPointType());
        trace.setPointSize(item.getPointSize());

        // Locate index of current Y Axis
        if (trace.getYAxis() != item.getAxisIndex())
            plot.moveTrace(trace, item.getAxisIndex());
        plot.requestUpdate();
    }

    /** @param item {@link ModelItem} for which to locate the {@link Trace}
     *  @return Trace
     *  @throws RuntimeException When trace not found
     */
    private Trace<Instant> findTrace(final ModelItem item)
    {
        for (Trace<Instant> trace : plot.getTraces())
            if (trace.getData() == item.getSamples())
                return trace;
        throw new IllegalArgumentException("Cannot locate trace for " + item);
    }

    /** @param trace {@link Trace} for which to locate the {@link ModelItem}
     *  @return ModelItem
     *  @throws RuntimeException When not found
     */
    private ModelItem findModelItem(final Trace<Instant> trace)
    {
        try
        {
            return items_by_trace.get(trace);
        }
        catch (Throwable ex)
        {
            throw new RuntimeException("Cannot locate item for " + trace, ex);
        }
    }

    /** Update plot to given time range.
     *  Can be called from any thread.
     *  @param start
     *  @param end
     */
    public void setTimeRange(final Instant start, final Instant end)
    {
        display.asyncExec(() -> plot.getXAxis().setValueRange(start, end));
    }

    /** Set annotations in plot to match model's annotations
     *  @param newAnnotations Annotations to show in plot
     */
    void setAnnotations(final Collection<AnnotationInfo> newAnnotations)
    {
        final List<Trace<Instant>> traces = new ArrayList<>();
        for (Trace<Instant> trace : plot.getTraces())
            traces.add(trace);

        // Remove old annotations from plot
        final List<Annotation<Instant>> plot_annotations = new ArrayList<>(plot.getAnnotations());
        for (Annotation<Instant> old : plot_annotations)
            plot.removeAnnotation(old);

        // Set new annotations in plot
        for (AnnotationInfo annotation : newAnnotations)
            plot.addAnnotation(new Annotation<Instant>(annotation.isInternal(),
                                                       traces.get(annotation.getItemIndex()),
                                                       annotation.getTime(),
                                                       annotation.getValue(),
                                                       annotation.getOffset(),
                                                       annotation.getText()));
    }

    /** Refresh the plot because the data has changed */
    public void redrawTraces()
    {
        plot.requestUpdate();
    }
}