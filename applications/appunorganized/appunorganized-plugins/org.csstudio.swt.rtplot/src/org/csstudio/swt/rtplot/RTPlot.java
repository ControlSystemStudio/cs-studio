/*******************************************************************************
 * Copyright (c) 2014-2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.data.PlotDataProvider;
import org.csstudio.swt.rtplot.internal.AnnotationImpl;
import org.csstudio.swt.rtplot.internal.MouseMode;
import org.csstudio.swt.rtplot.internal.Plot;
import org.csstudio.swt.rtplot.internal.SnapshotAction;
import org.csstudio.swt.rtplot.internal.ToggleLegendAction;
import org.csstudio.swt.rtplot.internal.ToggleToolbarAction;
import org.csstudio.swt.rtplot.internal.ToolbarHandler;
import org.csstudio.swt.rtplot.internal.TraceImpl;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;

/** Real-time plot
 *
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RTPlot<XTYPE extends Comparable<XTYPE>> extends Composite
{
    final protected Plot<XTYPE> plot;
    final protected ToolbarHandler<XTYPE> toolbar;
    final private ToggleToolbarAction<XTYPE> toggle_toolbar;
    final private ToggleLegendAction<XTYPE> toggle_legend;
    final private Action snapshot;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected RTPlot(final Composite parent, final Class<XTYPE> type)
    {
        super(parent, SWT.NULL);

        setLayout(new FormLayout());

        // To avoid unchecked casts, factory methods for..() would need
        // pass already constructed Plot<T> and Toolbar<T>, where T is set,
        // into constructor.
        // But they cannot be created outside of this Composite constructor
        // because they need parent == this.
        if (type == Double.class)
        {
            plot = (Plot) new Plot<Double>(this, Double.class);
            toolbar = (ToolbarHandler) new ToolbarHandler<Double>((RTPlot)this);
            toggle_toolbar = (ToggleToolbarAction) new ToggleToolbarAction<Double>((RTPlot)this, true);
            toggle_legend = (ToggleLegendAction) new ToggleLegendAction<Double>((RTPlot)this, true);
            snapshot = new SnapshotAction(this);
        }
        else if (type == Instant.class)
        {
            plot = (Plot) new Plot<Instant>(this, Instant.class);
            toolbar = (ToolbarHandler) new ToolbarHandler<Instant>((RTPlot)this);
            toggle_toolbar = (ToggleToolbarAction) new ToggleToolbarAction<Double>((RTPlot)this, true);
            toggle_legend = (ToggleLegendAction) new ToggleLegendAction<Double>((RTPlot)this, true);
            snapshot = new SnapshotAction(this);
        }
        else
            throw new IllegalArgumentException("Cannot handle " + type.getName());

        toolbar.addContextMenu(toggle_toolbar);

        FormData fd = new FormData();
        fd.top = new FormAttachment(toolbar.getControl());
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        plot.setLayoutData(fd);

        fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        toolbar.getControl().setLayoutData(fd);
    }

    /** @param listener Listener to add */
    public void addListener(final RTPlotListener<XTYPE> listener)
    {
        plot.addListener(listener);
    }

    /** @param listener Listener to remove */
    public void removeListener(final RTPlotListener<XTYPE> listener)
    {
        plot.removeListener(listener);
    }

    /** @return Control for the plot, to attach context menu */
    public Control getPlotControl()
    {
        return plot;
    }

    /** @return {@link Action} that can show/hide the legend */
    public Action getLegendAction()
    {
        return toggle_legend;
    }

    /** @return {@link Action} that can show/hide the toolbar */
    public Action getToolbarAction()
    {
        return toggle_toolbar;
    }

    /** @return {@link Action} that saves a snapshot of the plot */
    public Action getSnapshotAction()
    {
        return snapshot;
    }

    /** @param color Background color */
    public void setBackground(final RGB color)
    {
        plot.setBackground(Objects.requireNonNull(color));
    }

    /** Opacity (0 .. 100 %) of 'area' */
    public void setOpacity(final int opacity)
    {
        plot.setOpacity(opacity);
    }

    /** @param title Title text */
    public void setTitle(final Optional<String> title)
    {
        plot.setTitle(title);
    }

    /** @param font Font to use for title */
    public void setTitleFont(final FontData font)
    {
        plot.setTitleFont(Objects.requireNonNull(font));
    }

    /** @param font Font to use for labels */
    public void setLabelFont(final FontData font)
    {
        plot.setLabelFont(Objects.requireNonNull(font));
    }

    /** @param font Font to use for scale */
    public void setScaleFont(final FontData font)
    {
        plot.setScaleFont(Objects.requireNonNull(font));
    }

    /** @param font Font to use for legend */
    public void setLegendFont(final FontData font)
    {
        plot.setLegendFont(Objects.requireNonNull(font));
    }

    /** @return {@link Image} of current plot. Caller must dispose */
    public Image getImage()
    {
        return plot.getImage();
    }

    /** @return <code>true</code> if legend is visible */
    public boolean isLegendVisible()
    {
        return plot.isLegendVisible();
    }

    /** @param show <code>true</code> if legend should be displayed */
    public void showLegend(final boolean show)
    {
        if (isLegendVisible() == show)
            return;
        plot.showLegend(show);
        toggle_legend.updateText();
    }

    /** @return <code>true</code> if toolbar is visible */
    public boolean isToolbarVisible()
    {
        return toolbar.getControl().getVisible();
    }

    /** @param show <code>true</code> if toolbar should be displayed */
    public void showToolbar(final boolean show)
    {
        if (isToolbarVisible() == show)
            return;
        toolbar.getControl().setVisible(show);
        final FormData fd = new FormData();
        if (show)
            fd.top = new FormAttachment(toolbar.getControl());
        else
            fd.top = new FormAttachment(0);
        fd.left = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        plot.setLayoutData(fd);
        toggle_toolbar.updateText();
        layout();
        plot.fireToolbarChange(show);
    }

    /** Add a custom tool bar item
     *  @param style {@link SWT#PUSH}, {@link SWT#CHECK}
     *  @param icon Icon {@link Image}
     *  @param tool_tip Tool tip text
     *  @return {@link ToolItem}
     */
    public ToolItem addToolItem(final int style, final Image icon, final String tool_tip)
    {
        return toolbar.addItem(style, icon, tool_tip);
    }

    /** @param show Show the cross-hair cursor? */
    public void showCrosshair(final boolean show)
    {
        plot.showCrosshair(show);
    }

    /** Stagger the range of axes */
    public void stagger()
    {
        plot.stagger();
    }

    /** @param mode New {@link MouseMode}
     *  @throws IllegalArgumentException if mode is internal
     */
    public void setMouseMode(final MouseMode mode)
    {
        plot.setMouseMode(mode);
    }

    /** @return {@link UndoableActionManager} for this plot */
    public UndoableActionManager getUndoableActionManager()
    {
        return plot.getUndoableActionManager();
    }

    /** @return X/Time axis */
    public Axis<XTYPE> getXAxis()
    {
        return plot.getXAxis();
    }

    /** @return Boolean is point on x axis */
    public boolean inXAxis(final Point point)
    {
        return plot.inXAxis(point);
    }

    /** Add another Y axis
     *  @param name
     *  @return Y Axis that was added
     */
    public YAxis<XTYPE> addYAxis(final String name)
    {
        return plot.addYAxis(name);
    }

    /** @return Y axes */
    public List<YAxis<XTYPE>> getYAxes()
    {
        final List<YAxis<XTYPE>> result = new ArrayList<>();
        result.addAll(plot.getYAxes());
        return Collections.unmodifiableList(result);
    }

    /** @return Integer Index of y axis whose bounds point lies within;
     * -1 if no such axis exists */
    public int inYAxis(final Point point)
    {
        return plot.inYAxis(point);
    }

    /** @param index Index of Y axis to remove */
    public void removeYAxis(final int index)
    {
        plot.removeYAxis(index);
    }

    /** @param name Name, must not be <code>null</code>
     *  @param units Units, may be <code>null</code>
     *  @param data
     *  @param color
     *  @param type
     *  @param width
     *  @param y_axis
     *  @return {@link Trace} that was added
     */
    public Trace<XTYPE> addTrace(final String name, final String units,
            final PlotDataProvider<XTYPE> data,
            final RGB color,
            final TraceType type, final int width,
            final PointType point_type, final int size,
            final int y_axis)
    {
        final TraceImpl<XTYPE> trace = new TraceImpl<XTYPE>(name, units, data, color, type, width, point_type, size, y_axis);
        plot.addTrace(trace);
        return trace;
    }

    /** @return Thread-safe, read-only traces of the plot */
    public Iterable<Trace<XTYPE>> getTraces()
    {
        return plot.getTraces();
    }

    /** @return Count the number of traces */
    public int getTraceCount(){
    return plot.getTraceCount();
    }

    /** @param trace Trace to move from its current Y axis
     *  @param new_y_axis Index of new Y Axis
     */
    public void moveTrace(final Trace<XTYPE> trace, final int new_y_axis)
    {
        plot.moveTrace((TraceImpl<XTYPE>)trace, new_y_axis);
    }

    /** @param trace Trace to remove */
    public void removeTrace(final Trace<XTYPE> trace)
    {
        plot.removeTrace(trace);
    }

    /** Update the dormant time between updates
     *  @param dormant_time How long throttle remains dormant after a trigger
     *  @param unit Units for the dormant period
     */
    public void setUpdateThrottle(final long dormant_time, final TimeUnit unit)
    {
        plot.setUpdateThrottle(dormant_time, unit);
    }

    /** Request a complete redraw of the plot */
    public void requestUpdate()
    {
        plot.requestUpdate();
    }

    /** @param trace Trace to which an annotation should be added
     *  @param text Text for the annotation
     */
    public void addAnnotation(final Trace<XTYPE> trace, final String text)
    {
        plot.addAnnotation(trace, text);
    }

    /** @param annotation Annotation to add */
    public void addAnnotation(final Annotation<XTYPE> annotation)
    {
        plot.addAnnotation(annotation);
    }

    /** @return Current {@link AnnotationImpl}s */
    public List<Annotation<XTYPE>> getAnnotations()
    {
        return Collections.unmodifiableList(plot.getAnnotations());
    }

    /** Update text of annotation
     *  @param annotation {@link Annotation} to update.
     *         Must be an existing annotation obtained from <code>getAnnotations()</code>
     *  @param text New text
     *  @throws IllegalArgumentException if annotation is unknown
     */
    public void updateAnnotation(final Annotation<XTYPE> annotation, final String text)
    {
        plot.updateAnnotation(annotation, text);
    }

    /** @param annotation Annotation to remove */
    public void removeAnnotation(final Annotation<XTYPE> annotation)
    {
        plot.removeAnnotation(annotation);
    }
}
