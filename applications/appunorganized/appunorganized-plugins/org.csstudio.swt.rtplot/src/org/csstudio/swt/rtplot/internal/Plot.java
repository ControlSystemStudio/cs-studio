/*******************************************************************************
 * Copyright (c) 2014-2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.Annotation;
import org.csstudio.swt.rtplot.AxisRange;
import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.RTPlotListener;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.YAxis;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.internal.util.ScreenTransform;
import org.csstudio.swt.rtplot.undo.ChangeAxisRanges;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.swt.rtplot.undo.UpdateAnnotationAction;
import org.csstudio.swt.rtplot.util.UpdateThrottle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** Plot with axes and area that displays the traces
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Plot<XTYPE extends Comparable<XTYPE>> extends Canvas implements PaintListener, MouseListener, MouseMoveListener, MouseTrackListener
{
    final private static int ARROW_SIZE = 8;

    private static final double ZOOM_FACTOR = 1.5;

    /** When using 'rubberband' to zoom in, need to select a region
     *  at least this wide resp. high.
     *  Smaller regions are likely the result of an accidental
     *  click-with-jerk, which would result into a huge zoom step.
     */
    private static final int ZOOM_PIXEL_THRESHOLD = 20;

    /** Support for un-do and re-do */
    final private UndoableActionManager undo = new UndoableActionManager();

    final private SWTMediaPool media;

    /** Display */
    final private Display display;

    /** Background color */
    private volatile RGB background = new RGB(255, 255, 255);

    /** Opacity (0 .. 100 %) of 'area' */
    private volatile int opacity = 20;

    /** Font to use for, well, title */
    private volatile Font title_font;

    /** Font to use for labels */
    private volatile Font label_font;

    /** Font to use for scale */
    private volatile Font scale_font;

    /** Font to use for legend */
    private volatile Font legend_font;

    /** Area of this canvas */
    private volatile Rectangle area = new Rectangle(0, 0, 0, 0);

    /** Does layout need to be re-computed? */
    final private AtomicBoolean need_layout = new AtomicBoolean(true);

    /** Buffer for image of axes, plot area, but not mouse feedback.
     *
     *  <p>UpdateThrottle calls updateImageBuffer() to set the image
     *  in its thread, PaintListener draws it in UI thread,
     *  and getImage() may hand a safe copy for printing, saving, e-mailing.
     *
     *  <p>Synchronizing to access one and the same image
     *  deadlocks on Linux, so a new image is created for updates.
     *  To avoid access to disposed image, SYNC on the actual image during access.
     */
    private volatile Optional<Image> plot_image = Optional.empty();

    final private UpdateThrottle update_throttle;

    final private TitlePart title_part;
    final private List<Trace<XTYPE>> traces = new CopyOnWriteArrayList<>();
    final private AxisPart<XTYPE> x_axis;
    final private List<YAxisImpl<XTYPE>> y_axes = new CopyOnWriteArrayList<>();
    final private PlotPart plot_area;
    final private TracePainter<XTYPE> trace_painter = new TracePainter<XTYPE>();
    final private List<AnnotationImpl<XTYPE>> annotations = new CopyOnWriteArrayList<>();
    final private LegendPart<XTYPE> legend;

    final private PlotProcessor<XTYPE> plot_processor;

    final private Runnable redraw_runnable = () ->
    {
        if (isDisposed())
            return;
        try
        {
            redraw();
        }
        catch (SWTException ex)
        {
            if (! ex.getMessage().contains("disposed"))
                Activator.getLogger().log(Level.WARNING, "Redraw failed", ex);
        }
    };

    private boolean show_crosshair = false;
    private MouseMode mouse_mode = MouseMode.NONE;
    private Optional<Point> mouse_start = Optional.empty();
    private volatile Optional<Point> mouse_current = Optional.empty();
    private AxisRange<XTYPE> mouse_start_x_range;
    private List<AxisRange<Double>> mouse_start_y_ranges = new ArrayList<>();
    private int mouse_y_axis = -1;
    private Stack<Boolean> pre_pan_auto_scales = new Stack<Boolean>();

    // Annotation-related info. If mouse_annotation is set, the rest should be set.
    private Optional<AnnotationImpl<XTYPE>> mouse_annotation = Optional.empty();
    private Point mouse_annotation_start_offset;
    private XTYPE mouse_annotation_start_position;
    private double mouse_annotation_start_value;


    /** Listener to X Axis {@link PlotPart} */
    final PlotPartListener x_axis_listener = new PlotPartListener()
    {
        @Override
        public void layoutPlotPart(final PlotPart plotPart)
        {
            need_layout.set(true);
        }

        @Override
        public void refreshPlotPart(final PlotPart plotPart)
        {
            updateCursor();
            requestUpdate();
        }
    };
    /** Listener to Title, Y Axis and plot area {@link PlotPart}s */
    final PlotPartListener plot_part_listener = new PlotPartListener()
    {
        @Override
        public void layoutPlotPart(final PlotPart plotPart)
        {
            need_layout.set(true);
        }

        @Override
        public void refreshPlotPart(final PlotPart plotPart)
        {
            requestUpdate();
        }
    };

    final private List<RTPlotListener<XTYPE>> listeners = new CopyOnWriteArrayList<>();

    private volatile Optional<List<CursorMarker>> cursor_markers = Optional.empty();

    /** Constructor
     *  @param parent Parent widget
     *  @param type Type of X axis
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Plot(final Composite parent, final Class<XTYPE> type)
    {
        super(parent, SWT.NO_BACKGROUND);

        title_font = parent.getFont();
        label_font = parent.getFont();
        scale_font = parent.getFont();
        legend_font = parent.getFont();

        media = new SWTMediaPool(parent.getDisplay());
        display = parent.getDisplay();

        plot_processor = new PlotProcessor<XTYPE>(this);

        // To avoid unchecked cast, X axis would need to be passed in,
        // but its listener can only be created within this class.
        // When passing X axis in, its listener needs to be set
        // in here, but an axis design with final listener was preferred.
        if (type == Double.class)
            x_axis = (AxisPart) new HorizontalNumericAxis("X", x_axis_listener);
        else if (type == Instant.class)
            x_axis = (AxisPart) TimeAxis.forDuration("Time", x_axis_listener, Duration.ofMinutes(2));
        else
            throw new IllegalArgumentException("Cannot handle " + type.getName());

        addYAxis("Value 1");
        title_part = new TitlePart("", plot_part_listener);
        plot_area = new PlotPart("main", plot_part_listener);
        legend = new LegendPart<XTYPE>("legend", plot_part_listener);

        setMouseMode(MouseMode.PAN);

        // 50Hz default throttle
        update_throttle = new UpdateThrottle(50, TimeUnit.MILLISECONDS,
        () ->
        {
            plot_processor.autoscale();
            updateImageBuffer();
            redrawSafely();
        });

        addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(final ControlEvent e)
            {
                area = getClientArea();
                need_layout.set(true);
                requestUpdate();
            }
        });
        addPaintListener(this);
        addMouseListener(this);
        addMouseMoveListener(this);
        addMouseTrackListener(this);
        addDisposeListener((final DisposeEvent e) -> handleDisposal());
    }

    /** @param listener Listener to add */
    public void addListener(final RTPlotListener<XTYPE> listener)
    {
        Objects.requireNonNull(listener);
        listeners.add(listener);
    }

    /** @param listener Listener to remove */
    public void removeListener(final RTPlotListener<XTYPE> listener)
    {
        Objects.requireNonNull(listener);
        listeners.remove(listener);
    }

    /** @return {@link UndoableActionManager} for this plot */
    public UndoableActionManager getUndoableActionManager()
    {
        return undo;
    }

    /** @param color Background color */
    public void setBackground(final RGB color)
    {
        background = color;
    }

    /** Opacity (0 .. 100 %) of 'area' */
    public void setOpacity(final int opacity)
    {
        this.opacity = opacity;
    }

    /** @param title Title */
    public void setTitle(final Optional<String> title)
    {
        title_part.setName(title.orElse(""));
    }

    /** @param font Font to use for title */
    public void setTitleFont(final FontData font)
    {
        title_font = media.get(font);
        need_layout.set(true);
        requestUpdate();
    }

    /** @param font Font to use for labels */
    public void setLabelFont(final FontData font)
    {
        label_font = media.get(font);
        need_layout.set(true);
        requestUpdate();
    }

    /** @param font Font to use for scale */
    public void setScaleFont(final FontData font)
    {
        scale_font = media.get(font);
        need_layout.set(true);
        requestUpdate();
    }

    /** @return <code>true</code> if legend is visible */
    public boolean isLegendVisible()
    {
        return legend.isVisible();
    }

    /** @param show <code>true</code> if legend should be displayed */
    public void showLegend(final boolean show)
    {
        legend.setVisible(show);
        need_layout.set(true);
        requestUpdate();
        for (RTPlotListener<XTYPE> listener : listeners)
            listener.changedLegend(show);
    }

    /** @param font Font to use for scale */
    public void setLegendFont(final FontData font)
    {
        legend_font = media.get(font);
        need_layout.set(true);
        requestUpdate();
    }

    /** @return X/Time axis */
    public AxisPart<XTYPE> getXAxis()
    {
        return x_axis;
    }

    /** @return boolean is point on x axis */
    public boolean inXAxis(final Point point)
    {
        return x_axis.getBounds().contains(point);
    }

    /** Add another Y axis
     *  @param name
     *  @return Y Axis that was added
     */
    public YAxis<XTYPE> addYAxis(final String name)
    {
        YAxisImpl<XTYPE> axis = new YAxisImpl<XTYPE>(name, plot_part_listener);
        y_axes.add(axis);
        need_layout.set(true);
        return axis;
    }

    /** @return Y axes */
    public List<YAxisImpl<XTYPE>> getYAxes()
    {
        final List<YAxisImpl<XTYPE>> copy = new ArrayList<>();
        copy.addAll(y_axes);
        return copy;
    }

    /** @return Integer Index of y axis whose bounds point lies within;
     * -1 if no such axis exists */
    public int inYAxis(final Point point)
    {
        int n = 0;
        for(YAxisImpl<XTYPE> y_axis: y_axes) {
            if(y_axis.getBounds().contains(point))
                return n;
            n += 1;
        }
        return -1;
    }

    /** @param index Index of Y axis to remove */
    public void removeYAxis(final int index)
    {
        y_axes.remove(index);
        need_layout.set(true);
    }

    /** Add trace to the plot
     *  @param trace {@link Trace}, where axis must be a valid Y axis index
     */
    public void addTrace(final TraceImpl<XTYPE> trace)
    {
        traces.add(trace);
        y_axes.get(trace.getYAxis()).addTrace(trace);
        need_layout.set(true);
        requestUpdate();
    }

    /** @param trace Trace to move from its current Y axis
     *  @param new_y_axis Index of new Y Axis
     */
    public void moveTrace(final TraceImpl<XTYPE> trace, final int new_y_axis)
    {
        Objects.requireNonNull(trace);
        y_axes.get(trace.getYAxis()).removeTrace(trace);
        trace.setYAxis(new_y_axis);
        y_axes.get(trace.getYAxis()).addTrace(trace);
    }

    /** @return Thread-safe, read-only traces of the plot */
    public Iterable<Trace<XTYPE>> getTraces()
    {
        return traces;
    }

    /** @return Count the number of traces */
    public int getTraceCount(){
    return traces.size();
    }

    /** Remove trace from plot
     *  @param trace {@link Trace}, where axis must be a valid Y axis index
     */
    public void removeTrace(final Trace<XTYPE> trace)
    {
        Objects.requireNonNull(trace);
        traces.remove(trace);
        y_axes.get(trace.getYAxis()).removeTrace(trace);
        need_layout.set(true);
        requestUpdate();
    }

    /** @return {@link Image} of current plot. Caller must dispose */
    public Image getImage()
    {
        // Using locking to protect plot_image can cause deadlocks due to a
        // bug in SWT: https://bugs.eclipse.org/bugs/show_bug.cgi?id=265265
        // Instead, just try again if the image was disposed.
        // Is there still a canvas that will generate an image?
        if (! isDisposed())
            while (true)
            {
                Image image = plot_image.orElse(null);
                if (image == null)
                    break;
                try
                {
                    return new Image(display, image, SWT.IMAGE_COPY);
                }
                catch (Throwable ex)
                {
                    // If image was disposed, try again
                    // Otherwise give up
                    if (! image.isDisposed())
                        break;
                }
            }
        return new Image(display, 10, 10);
    }

    /** Update the dormant time between updates
     *  @param dormant_time How long throttle remains dormant after a trigger
     *  @param unit Units for the dormant period
     */
    public void setUpdateThrottle(final long dormant_time, final TimeUnit unit)
    {
        update_throttle.setDormantTime(dormant_time, unit);
    }

    /** Request a complete redraw of the plot */
    final public void requestUpdate()
    {
        update_throttle.trigger();
    }

    /** Redraw the current image and cursors
     *
     *  <p>Like <code>redraw()</code>, but may be called
     *  from any thread.
     */
    final void redrawSafely()
    {
        if (! display.isDisposed())
            display.asyncExec(redraw_runnable);
    }

    /** Add Annotation to a trace,
     *  determining initial position based on some
     *  sample of that trace
     *  @param trace Trace to which a Annotation should be added
     *  @param text Text for the annotation
     */
    public void addAnnotation(final Trace<XTYPE> trace, final String text)
    {
        Objects.requireNonNull(trace);
        plot_processor.createAnnotation(trace, text);
    }

    /** @param annotation Annotation to add */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addAnnotation(final Annotation<XTYPE> annotation)
    {
        Objects.requireNonNull(annotation);
        if (annotation instanceof AnnotationImpl)
            annotations.add((AnnotationImpl)annotation);
        else
            annotations.add(new AnnotationImpl<XTYPE>(annotation.isInternal(), annotation.getTrace(), annotation.getPosition(),
                                                      annotation.getValue(), annotation.getOffset(),
                                                      annotation.getText()));
        requestUpdate();
        fireAnnotationsChanged();
    }

    /** @return Current {@link AnnotationImpl}s */
    public List<AnnotationImpl<XTYPE>> getAnnotations()
    {
        return annotations;
    }

    /** Update location and value of annotation
     *  @param annotation {@link AnnotationImpl} to update
     *  @param position New position
     *  @param value New value
     */
    public void updateAnnotation(final AnnotationImpl<XTYPE> annotation, final XTYPE position, final double value,
                                 final Point offset)
    {
        annotation.setLocation(position, value);
        annotation.setOffset(offset);
        requestUpdate();
        fireAnnotationsChanged();
    }

    /** Update text of annotation
     *  @param annotation {@link Annotation} to update.
     *         Must be an existing annotation obtained from <code>getAnnotations()</code>
     *  @param text New text
     *  @throws IllegalArgumentException if annotation is unknown
     */
    public void updateAnnotation(final Annotation<XTYPE> annotation, final String text)
    {
        final int index = annotations.indexOf(annotation);
        if (index < 0)
            throw new IllegalArgumentException("Unknown annotation " + annotation);
        annotations.get(index).setText(text);
        requestUpdate();
        fireAnnotationsChanged();
    }

    /** @param annotation Annotation to remove */
    public void removeAnnotation(final Annotation<XTYPE> annotation)
    {
        annotations.remove(annotation);
        requestUpdate();
        fireAnnotationsChanged();
    }

    /** Select Annotation at mouse position?
     *  @return Was a mouse annotation set?
     */
    private boolean selectMouseAnnotation()
    {
        if (mouse_start.isPresent())
            for (AnnotationImpl<XTYPE> annotation : annotations)
                if (annotation.isSelected(mouse_start.get()))
                {
                    mouse_annotation_start_offset = annotation.getOffset();
                    mouse_annotation_start_position = annotation.getPosition();
                    mouse_annotation_start_value = annotation.getValue();
                    mouse_annotation = Optional.of(annotation);
                    requestUpdate();
                    return true;
                }
        return false;
    }

    /** De-select an Annotation */
    private void deselectMouseAnnotation()
    {
        if (mouse_annotation.isPresent())
        {
            AnnotationImpl<XTYPE> anno = mouse_annotation.get();
            undo.add(new UpdateAnnotationAction<XTYPE>(this, anno,
                    mouse_annotation_start_position, mouse_annotation_start_value,
                    mouse_annotation_start_offset,
                    anno.getPosition(), anno.getValue(),
                    anno.getOffset()));
            anno.deselect();
            mouse_annotation = Optional.empty();
            requestUpdate();
        }
    }

    /** Compute layout of plot components */
    private void computeLayout(final GC gc, final Rectangle bounds)
    {
        // Title on top, as high as desired
        final int title_height = title_part.getDesiredHeight(gc, title_font);
        title_part.setBounds(0, 0, bounds.width, title_height);

        // Legend on bottom, as high as desired
        final int legend_height = legend.getDesiredHeight(gc, bounds.width, legend_font, traces);
        legend.setBounds(0,  bounds.height-legend_height, bounds.width, legend_height);

        // X Axis as high as desired. Width will depend on Y axes.
        x_axis.setLabelFont(label_font.getFontData()[0]);
        x_axis.setScaleFont(scale_font.getFontData()[0]);
        final int x_axis_height = x_axis.getDesiredPixelSize(bounds, gc);
        final int y_axis_height = bounds.height - title_height - x_axis_height - legend_height;

        // Ask each Y Axis for its widths, which changes based on number of labels
        // and how they are laid out
        int total_left_axes_width = 0, total_right_axes_width = 0;
        int plot_width = bounds.width;

        final List<YAxisImpl<XTYPE>> save_copy = new ArrayList<>(y_axes);
        // First, lay out 'left' axes in reverse order to get "2, 1, 0" on the left of the plot.
        for (YAxisImpl<XTYPE> axis : save_copy)
            if (! axis.isOnRight())
            {
                final Rectangle axis_region = new Rectangle(total_left_axes_width, title_height, plot_width, y_axis_height);
                axis.setLabelFont(label_font.getFontData()[0]);
                axis.setScaleFont(scale_font.getFontData()[0]);
                axis_region.width = axis.getDesiredPixelSize(axis_region, gc);
                axis.setBounds(axis_region);
                total_left_axes_width += axis_region.width;
                plot_width -= axis_region.width;
            }
        // Then lay out 'right' axes, also in reverse order, to get "0, 1, 2" on right side of plot.
        for (YAxisImpl<XTYPE> axis : save_copy)
            if (axis.isOnRight())
            {
                final Rectangle axis_region = new Rectangle(total_left_axes_width, title_height, plot_width, y_axis_height);
                axis.setLabelFont(label_font.getFontData()[0]);
                axis.setScaleFont(scale_font.getFontData()[0]);
                axis_region.width = axis.getDesiredPixelSize(axis_region, gc);
                total_right_axes_width += axis_region.width;
                axis_region.x = bounds.width - total_right_axes_width;
                axis.setBounds(axis_region);
                plot_width -= axis_region.width;
            }

        x_axis.setBounds(total_left_axes_width, title_height+y_axis_height, plot_width, x_axis_height);

        plot_area.setBounds(total_left_axes_width, title_height, plot_width, y_axis_height);
    }

    /** Draw all components into image buffer */
    private void updateImageBuffer()
    {
        final Rectangle area_copy = area;
        if (area_copy.width <= 0  ||  area_copy.height <= 0)
            return;

        final Image image = new Image(display, area_copy);
        final GC gc = new GC(image);

        if (need_layout.getAndSet(false))
            computeLayout(gc, area_copy);

        final Rectangle plot_bounds = plot_area.getBounds();

        gc.setBackground(media.get(background));
        gc.fillRectangle(area_copy);

        title_part.paint(gc, media, title_font);
        legend.paint(gc, media, legend_font, traces);

        // Fetch x_axis transformation and use that to paint all traces,
        // because X Axis tends to change from scrolling
        // while we're painting traces
//        x_axis.setLabelFont(label_font);
//        x_axis.setScaleFont(scale_font);
        x_axis.paint(gc, media, plot_bounds);
        final ScreenTransform<XTYPE> x_transform = x_axis.getScreenTransform();
        for (YAxisImpl<XTYPE> y_axis : y_axes)
        {
//            y_axis.setLabelFont(label_font);
//            y_axis.setScaleFont(scale_font);
            y_axis.paint(gc, media, plot_bounds);
        }

        gc.setClipping(plot_bounds);
        plot_area.paint(gc, media);

        for (YAxisImpl<XTYPE> y_axis : y_axes)
            for (Trace<XTYPE> trace : y_axis.getTraces())
                trace_painter.paint(gc, media, plot_area.getBounds(), opacity, x_transform, y_axis, trace);

        // Annotations use label font
        gc.setFont(label_font);
        for (AnnotationImpl<XTYPE> annotation : annotations)
            annotation.paint(gc, media, x_axis, y_axes.get(annotation.getTrace().getYAxis()));

        gc.dispose();

        // Update image
        final Image old_image = plot_image.orElse(null);
        plot_image = Optional.of(image);
        if (old_image != null)
        {
            synchronized (old_image)
            {
                old_image.dispose();
            }
        }
    }

    /** PaintListener: {@inheritDoc} */
    @Override
    public void paintControl(final PaintEvent e)
    {
        Activator.getLogger().finer("paint");
        final GC gc = e.gc;
        final Image image = plot_image.orElse(null);
        if (image != null)
            synchronized (image)
            {
                gc.drawImage(image, 0, 0);
            }
        drawMouseModeFeedback(gc);
    }

    /** Draw visual feedback (rubber band rectangle etc.)
     *  for current mouse mode
     *  @param gc GC
     */
    private void drawMouseModeFeedback(final GC gc)
    {   // Safe copy, then check null (== isPresent())
        final Point current = mouse_current.orElse(null);
        if (current == null)
            return;

        final Point start = mouse_start.orElse(null);

        final Rectangle plot_bounds = plot_area.getBounds();

        if (mouse_mode == MouseMode.PAN_X  ||  mouse_mode == MouseMode.PAN_Y || mouse_mode == MouseMode.PAN_PLOT)
        {
            // NOP, minimize additional UI thread drawing to allow better 'pan' updates
        }
        else if (show_crosshair  &&  plot_bounds.contains(current))
        {   // Cross-hair Cursor
            gc.drawLine(area.x, current.y, area.x + area.width, current.y);
            gc.drawLine(current.x, area.y, current.x, area.y + area.height);
            // Corresponding axis ticks
            gc.setBackground(media.get(background));
            x_axis.drawTickLabel(gc, media, x_axis.getValue(current.x), true);
            for (YAxisImpl<XTYPE> axis : y_axes)
                axis.drawTickLabel(gc, media, axis.getValue(current.y), true);
            // Trace markers
            final List<CursorMarker> safe_markers = cursor_markers.orElse(null);
            if (safe_markers != null)
                CursorMarker.drawMarkers(gc, media, safe_markers, area);
        }

        if (mouse_mode == MouseMode.ZOOM_IN  ||  mouse_mode == MouseMode.ZOOM_OUT)
        {   // Update mouse pointer in read-to-zoom mode
            if (plot_bounds.contains(current))
                setCursor(display.getSystemCursor(SWT.CURSOR_SIZEALL));
            else if (x_axis.getBounds().contains(current))
                setCursor(display.getSystemCursor(SWT.CURSOR_SIZEWE));
            else
            {
                for (YAxisImpl<XTYPE> axis : y_axes)
                    if (axis.getBounds().contains(current))
                    {
                        setCursor(display.getSystemCursor(SWT.CURSOR_SIZENS));
                        return;
                    }
                setCursor(display.getSystemCursor(SWT.CURSOR_NO));
            }
        }
        else if (mouse_mode == MouseMode.ZOOM_IN_X  &&  start != null)
        {
            final int left = Math.min(start.x, current.x);
            final int right = Math.max(start.x, current.x);
            final int width = right - left;
            final int mid_y = plot_bounds.y + plot_bounds.height / 2;
            // Range on axis
            gc.drawRectangle(left, start.y, width, 1);
            // Left, right vertical bar
            gc.drawLine(left, plot_bounds.y, left, plot_bounds.y + plot_bounds.height);
            gc.drawLine(right, plot_bounds.y, right, plot_bounds.y + plot_bounds.height);
            if (width >= 5*ARROW_SIZE)
            {
                gc.drawLine(left, mid_y, left + 2*ARROW_SIZE, mid_y);
                gc.drawLine(left+ARROW_SIZE, mid_y-ARROW_SIZE, left + 2*ARROW_SIZE, mid_y);
                gc.drawLine(left+ARROW_SIZE, mid_y+ARROW_SIZE, left + 2*ARROW_SIZE, mid_y);

                gc.drawLine(right, mid_y, right - 2*ARROW_SIZE, mid_y);
                gc.drawLine(right-ARROW_SIZE, mid_y-ARROW_SIZE, right - 2*ARROW_SIZE, mid_y);
                gc.drawLine(right-ARROW_SIZE, mid_y+ARROW_SIZE, right - 2*ARROW_SIZE, mid_y);
            }
        }
        else if (mouse_mode == MouseMode.ZOOM_IN_Y  &&  start != null)
        {
            final int top = Math.min(start.y, current.y);
            final int bottom = Math.max(start.y, current.y);
            final int height = bottom - top;
            final int mid_x = plot_bounds.x + plot_bounds.width / 2;
            // Range on axis
            gc.drawRectangle(start.x, top, 1, height);
            // Top, bottom horizontal bar
            gc.drawLine(plot_bounds.x, top, plot_bounds.x + plot_bounds.width, top);
            gc.drawLine(plot_bounds.x, bottom, plot_bounds.x + plot_bounds.width, bottom);
            if (height >= 5 * ARROW_SIZE)
            {
                gc.drawLine(mid_x, top, mid_x, top + 2*ARROW_SIZE);
                gc.drawLine(mid_x-ARROW_SIZE, top+ARROW_SIZE, mid_x, top + 2*ARROW_SIZE);
                gc.drawLine(mid_x+ARROW_SIZE, top+ARROW_SIZE, mid_x, top + 2*ARROW_SIZE);

                gc.drawLine(mid_x, bottom - 2*ARROW_SIZE, mid_x, bottom);
                gc.drawLine(mid_x, bottom - 2*ARROW_SIZE, mid_x-ARROW_SIZE, bottom - ARROW_SIZE);
                gc.drawLine(mid_x, bottom - 2*ARROW_SIZE, mid_x+ARROW_SIZE, bottom - ARROW_SIZE);
            }
        }
        else if (mouse_mode == MouseMode.ZOOM_IN_PLOT  &&  start != null)
        {
            final int left = Math.min(start.x, current.x);
            final int right = Math.max(start.x, current.x);
            final int top = Math.min(start.y, current.y);
            final int bottom = Math.max(start.y, current.y);
            final int width = right - left;
            final int height = bottom - top;
            final int mid_x = left + width / 2;
            final int mid_y = top + height / 2;
            gc.drawRectangle(left, top, width, height);
            if (width >= 5*ARROW_SIZE)
            {
                gc.drawLine(left, mid_y, left + 2*ARROW_SIZE, mid_y);
                gc.drawLine(left+ARROW_SIZE, mid_y-ARROW_SIZE, left + 2*ARROW_SIZE, mid_y);
                gc.drawLine(left+ARROW_SIZE, mid_y+ARROW_SIZE, left + 2*ARROW_SIZE, mid_y);

                gc.drawLine(right, mid_y, right - 2*ARROW_SIZE, mid_y);
                gc.drawLine(right-ARROW_SIZE, mid_y-ARROW_SIZE, right - 2*ARROW_SIZE, mid_y);
                gc.drawLine(right-ARROW_SIZE, mid_y+ARROW_SIZE, right - 2*ARROW_SIZE, mid_y);
            }
            if (height >= 5*ARROW_SIZE)
            {
                gc.drawLine(mid_x, top, mid_x, top + 2*ARROW_SIZE);
                gc.drawLine(mid_x-ARROW_SIZE, top+ARROW_SIZE, mid_x, top + 2*ARROW_SIZE);
                gc.drawLine(mid_x+ARROW_SIZE, top+ARROW_SIZE, mid_x, top + 2*ARROW_SIZE);

                gc.drawLine(mid_x, bottom - 2*ARROW_SIZE, mid_x, bottom);
                gc.drawLine(mid_x, bottom - 2*ARROW_SIZE, mid_x-ARROW_SIZE, bottom - ARROW_SIZE);
                gc.drawLine(mid_x, bottom - 2*ARROW_SIZE, mid_x+ARROW_SIZE, bottom - ARROW_SIZE);
            }
        }
    }

    /** Invoked as {@link DisposeListener} */
    private void handleDisposal()
    {   // Stop updates which could otherwise still use
        // what's about to be disposed
        update_throttle.dispose();
        // .. then release resources
        for (PlotPart y_axis : y_axes)
            y_axis.dispose();
        x_axis.dispose();
        plot_area.dispose();

        final Image old_image = plot_image.orElse(null);
        plot_image = Optional.empty();
        if (old_image != null)
            synchronized (old_image)
            {
                old_image.dispose();
            }
        media.dispose();
    }

    /** @param show Show the cross-hair cursor? */
    public void showCrosshair(final boolean show)
    {
        show_crosshair = show;
    }

    /** @param mode New {@link MouseMode}
     *  @throws IllegalArgumentException if mode is internal
     */
    public void setMouseMode(final MouseMode mode)
    {
        if (mode.ordinal() >= MouseMode.INTERNAL_MODES.ordinal())
            throw new IllegalArgumentException("Not permitted to set " + mode);
        mouse_mode = mode;

        // Selecting system cursor.
        // Custom cursors are not supported with RAP..
        switch (mode)
        {
        case PAN:
            setCursor(display.getSystemCursor(SWT.CURSOR_HAND));
            break;
        case ZOOM_IN:
            setCursor(display.getSystemCursor(SWT.CURSOR_CROSS));
            break;
        default:
            setCursor(display.getSystemCursor(SWT.CURSOR_ARROW));
        }
    }

    /** MouseListener: {@inheritDoc} */
    @Override
    public void mouseDown(final MouseEvent e)
    {
        // Don't start mouse actions when user invokes context menu
        if (e.button != 1  ||  e.stateMask != 0)
            return;
        final Point current = new Point(e.x, e.y);
        mouse_start = mouse_current = Optional.of(current);

        if (selectMouseAnnotation())
            return;
        else if (mouse_mode == MouseMode.PAN)
        {   // Determine start of 'pan'
            mouse_start_x_range = x_axis.getValueRange();
            mouse_start_y_ranges.clear();
            for (int i=0; i<y_axes.size(); ++i)
            {
                final YAxisImpl<XTYPE> axis = y_axes.get(i);
                mouse_start_y_ranges.add(axis.getValueRange());
                if (axis.getBounds().contains(current))
                {
                    mouse_y_axis = i;
                    mouse_mode = MouseMode.PAN_Y;

                    // Store the auto scale state during mouse actions,
                    // we can use it later to create an un-doable action.
                    pre_pan_auto_scales.push(axis.isAutoscale());
                    axis.setAutoscale(false);
                    fireAutoScaleChange(axis);
                    return;
                }
            }
            if (plot_area.getBounds().contains(current))
            {
                mouse_mode = MouseMode.PAN_PLOT;
                for (YAxisImpl<XTYPE> axis : y_axes)
                {
                    pre_pan_auto_scales.push(axis.isAutoscale());
                    axis.setAutoscale(false);
                    fireAutoScaleChange(axis);
                }
            }
            else if (x_axis.getBounds().contains(current))
                mouse_mode = MouseMode.PAN_X;
        }
        else if (mouse_mode == MouseMode.ZOOM_IN)
        {   // Determine start of 'rubberband' zoom.
            // Reset cursor from SIZE* to CROSS.
            for (int i=0; i<y_axes.size(); ++i)
                if (y_axes.get(i).getBounds().contains(current))
                {
                    mouse_y_axis = i;
                    mouse_mode = MouseMode.ZOOM_IN_Y;
                    setCursor(display.getSystemCursor(SWT.CURSOR_CROSS));
                    return;
                }
            if (plot_area.getBounds().contains(current))
            {
                mouse_mode = MouseMode.ZOOM_IN_PLOT;
                setCursor(display.getSystemCursor(SWT.CURSOR_CROSS));
            }
            else if (x_axis.getBounds().contains(current))
            {
                mouse_mode = MouseMode.ZOOM_IN_X;
                setCursor(display.getSystemCursor(SWT.CURSOR_CROSS));
            }
        }
        else if (mouse_mode == MouseMode.ZOOM_OUT)
        {   // Zoom 'out' from where the mouse was clicked
            if (x_axis.getBounds().contains(current))
            {   // Zoom out of X axis
                final AxisRange<XTYPE> orig = x_axis.getValueRange();
                x_axis.zoom(current.x, ZOOM_FACTOR);
                undo.add(new ChangeAxisRanges<XTYPE>(this, Messages.Zoom_Out_X, x_axis, orig, x_axis.getValueRange()));
                fireXAxisChange();
            }
            else if (plot_area.getBounds().contains(current))
            {   // Zoom out of X..
                final AxisRange<XTYPE> orig_x = x_axis.getValueRange();
                x_axis.zoom(current.x, ZOOM_FACTOR);
                fireXAxisChange();
                // .. and Y axes
                final List<AxisRange<Double>> old_range = new ArrayList<>(y_axes.size());
                final List<AxisRange<Double>> new_range = new ArrayList<>(y_axes.size());
                final List<Boolean> original_autoscale = new ArrayList<>(y_axes.size());
                final List<Boolean> new_autoscale = new ArrayList<>(y_axes.size());
                for (YAxisImpl<XTYPE> axis : y_axes)
                {
                      original_autoscale.add(axis.isAutoscale());
                      new_autoscale.add(Boolean.FALSE);
                      old_range.add(axis.getValueRange());
                      axis.zoom(current.y, ZOOM_FACTOR);
                      new_range.add(axis.getValueRange());
                      fireYAxisChange(axis);
                }
                undo.execute(new ChangeAxisRanges<XTYPE>(this, Messages.Zoom_Out,
                        x_axis, orig_x, x_axis.getValueRange(), y_axes, old_range, new_range,
                        original_autoscale, new_autoscale));
            }
            else
            {   // Zoom out of Y axis
                for (YAxisImpl<XTYPE> axis : y_axes)
                    if (axis.getBounds().contains(current))
                    {
                        final AxisRange<Double> orig = axis.getValueRange();
                        final Boolean original_autoscale = axis.isAutoscale();
                        axis.setAutoscale(false);
                        fireAutoScaleChange(axis);
                        axis.zoom(current.y, ZOOM_FACTOR);
                        fireYAxisChange(axis);
                        undo.add(new ChangeAxisRanges<XTYPE>(this, Messages.Zoom_Out_Y,
                                Arrays.asList(axis),
                                Arrays.asList(orig),
                                Arrays.asList(axis.getValueRange()),
                                Arrays.asList(original_autoscale),
                                Arrays.asList(Boolean.FALSE)));
                        break;
                    }
            }
        }
    }

    /** MouseMoveListener: {@inheritDoc} */
    @Override
    public void mouseMove(final MouseEvent e)
    {
        final Point current = new Point(e.x, e.y);
        mouse_current = Optional.of(current);

        final Point start = mouse_start.orElse(null);

        if (mouse_annotation.isPresent()  &&  start != null)
        {
            final AnnotationImpl<XTYPE> anno = mouse_annotation.get();
            if (anno.getSelection() == AnnotationImpl.Selection.Body)
            {
                anno.setOffset(
                        new Point(mouse_annotation_start_offset.x + current.x - start.x,
                                  mouse_annotation_start_offset.y + current.y - start.y));
                requestUpdate();
                fireAnnotationsChanged();
            }
            else
                plot_processor.updateAnnotation(anno, x_axis.getValue(current.x));
        }
        else if (mouse_mode == MouseMode.PAN_X  &&  start != null)
            x_axis.pan(mouse_start_x_range, x_axis.getValue(start.x), x_axis.getValue(current.x));
        else if (mouse_mode == MouseMode.PAN_Y  &&  start != null)
        {
            final YAxisImpl<XTYPE> axis = y_axes.get(mouse_y_axis);
            axis.pan(mouse_start_y_ranges.get(mouse_y_axis), axis.getValue(start.y), axis.getValue(current.y));
        }
        else if (mouse_mode == MouseMode.PAN_PLOT  &&  start != null)
        {
            x_axis.pan(mouse_start_x_range, x_axis.getValue(start.x), x_axis.getValue(current.x));
            for (int i=0; i<y_axes.size(); ++i)
            {
                final YAxisImpl<XTYPE> axis = y_axes.get(i);
                axis.pan(mouse_start_y_ranges.get(i), axis.getValue(start.y), axis.getValue(current.y));
            }
        }
        else
            updateCursor();
    }

    /** Request update of cursor markers */
    private void updateCursor()
    {
        final Point current = mouse_current.orElse(null);
        if (current == null)
            return;
        final int x = current.x;
        final XTYPE location = x_axis.getValue(x);
        plot_processor.updateCursorMarkers(x, location, this::updateCursors);
    }

    /** Called by {@link PlotProcessor}
     *  @param markers Markes for current cursor position
     */
    private void updateCursors(final List<CursorMarker> markers)
    {
        cursor_markers  = Optional.ofNullable(markers);
        redrawSafely();
        fireCursorsChanged();
    }

    /** MouseListener: {@inheritDoc} */
    @Override
    public void mouseUp(final MouseEvent e)
    {
        deselectMouseAnnotation();

        final Point start = mouse_start.orElse(null);
        final Point current = mouse_current.orElse(null);
        if (start == null  ||  current == null)
            return;

        if (mouse_mode == MouseMode.PAN_X)
        {
            mouseMove(e);
            undo.add(new ChangeAxisRanges<XTYPE>(this, Messages.Pan_X, x_axis, mouse_start_x_range, x_axis.getValueRange()));
            fireXAxisChange();
            mouse_mode = MouseMode.PAN;
        }
        else if (mouse_mode == MouseMode.PAN_Y)
        {
            mouseMove(e);
            final YAxisImpl<XTYPE> y_axis = y_axes.get(mouse_y_axis);
            List<Boolean> new_autoscales = Arrays.asList(new Boolean[1]);
            Collections.fill(new_autoscales, Boolean.FALSE);
            undo.add(new ChangeAxisRanges<XTYPE>(this, Messages.Pan_Y,
                    Arrays.asList(y_axis),
                    Arrays.asList(mouse_start_y_ranges.get(mouse_y_axis)),
                    Arrays.asList(y_axis.getValueRange()),
                    pre_pan_auto_scales,
                    new_autoscales));
            pre_pan_auto_scales = new Stack<Boolean>();  // Clear cache of autoscales
            fireYAxisChange(y_axis);
            mouse_mode = MouseMode.PAN;
        }
        else if (mouse_mode == MouseMode.PAN_PLOT)
        {
            mouseMove(e);
            List<AxisRange<Double>> current_y_ranges = new ArrayList<>();
            for (YAxisImpl<XTYPE> axis : y_axes)
                current_y_ranges.add(axis.getValueRange());
            List<Boolean> new_autoscales = Arrays.asList(new Boolean[y_axes.size()]);
            Collections.fill(new_autoscales, Boolean.FALSE);
            undo.add(new ChangeAxisRanges<XTYPE>(this, Messages.Pan,
                    x_axis, mouse_start_x_range, x_axis.getValueRange(),
                    y_axes, mouse_start_y_ranges, current_y_ranges,
                    pre_pan_auto_scales, new_autoscales));
            pre_pan_auto_scales = new Stack<Boolean>();  // Clear cache of autoscales
            fireXAxisChange();
            for (YAxisImpl<XTYPE> axis : y_axes)
                fireYAxisChange(axis);
            mouse_mode = MouseMode.PAN;
        }
        else if (mouse_mode == MouseMode.ZOOM_IN_X)
        {   // X axis increases going _right_ just like mouse 'x' coordinate
            if (Math.abs(start.x - current.x) > ZOOM_PIXEL_THRESHOLD)
            {
                int low = Math.min(start.x, current.x);
                int high = Math.max(start.x, current.x);
                final AxisRange<XTYPE> original_x_range = x_axis.getValueRange();
                final AxisRange<XTYPE> new_x_range = new AxisRange<>(x_axis.getValue(low), x_axis.getValue(high));
                undo.execute(new ChangeAxisRanges<XTYPE>(this, Messages.Zoom_In_X, x_axis, original_x_range, new_x_range));
            }
            mouse_mode = MouseMode.ZOOM_IN;
        }
        else if (mouse_mode == MouseMode.ZOOM_IN_Y)
        {   // Mouse 'y' increases going _down_ the screen
            if (Math.abs(start.y - current.y) > ZOOM_PIXEL_THRESHOLD)
            {
                final int high = Math.min(start.y, current.y);
                final int low = Math.max(start.y, current.y);
                final YAxisImpl<XTYPE> axis = y_axes.get(mouse_y_axis);
                undo.execute(new ChangeAxisRanges<XTYPE>(this, Messages.Zoom_In_Y,
                        Arrays.asList(axis),
                        Arrays.asList(axis.getValueRange()),
                        Arrays.asList(new AxisRange<Double>(axis.getValue(low), axis.getValue(high))),
                        Arrays.asList(axis.isAutoscale()),
                        Arrays.asList(Boolean.FALSE)));
            }
            mouse_mode = MouseMode.ZOOM_IN;
        }
        else if (mouse_mode == MouseMode.ZOOM_IN_PLOT)
        {
            if (Math.abs(start.x - current.x) > ZOOM_PIXEL_THRESHOLD  ||
                Math.abs(start.y - current.y) > ZOOM_PIXEL_THRESHOLD)
            {   // X axis increases going _right_ just like mouse 'x' coordinate
                int low = Math.min(start.x, current.x);
                int high = Math.max(start.x, current.x);
                final AxisRange<XTYPE> original_x_range = x_axis.getValueRange();
                final AxisRange<XTYPE> new_x_range = new AxisRange<>(x_axis.getValue(low), x_axis.getValue(high));

                // Mouse 'y' increases going _down_ the screen
                final List<AxisRange<Double>> original_y_ranges = new ArrayList<>();
                final List<AxisRange<Double>> new_y_ranges = new ArrayList<>();
                final List<Boolean> original_autoscale_values = new ArrayList<>();
                final List<Boolean> new_autoscale_values = new ArrayList<>();
                high = Math.min(start.y, current.y);
                low = Math.max(start.y, current.y);
                for (YAxisImpl<XTYPE> axis : y_axes)
                {
                    original_y_ranges.add(axis.getValueRange());
                    new_y_ranges.add(new AxisRange<Double>(axis.getValue(low), axis.getValue(high)));
                    original_autoscale_values.add(axis.isAutoscale());
                    new_autoscale_values.add(Boolean.FALSE);
                }
                undo.execute(new ChangeAxisRanges<XTYPE>(this, Messages.Zoom_In, x_axis, original_x_range, new_x_range, y_axes, original_y_ranges, new_y_ranges, original_autoscale_values, new_autoscale_values));
            }
            mouse_mode = MouseMode.ZOOM_IN;
        }
    }

    /** MouseListener: {@inheritDoc} */
    @Override
    public void mouseDoubleClick(final MouseEvent e)
    {
        // NOP
    }

    /** MouseTrackListener: {@inheritDoc} */
    @Override
    public void mouseEnter(final MouseEvent e)
    {
        // NOP
    }

    /** MouseTrackListener: {@inheritDoc} */
    @Override
    public void mouseExit(final MouseEvent e)
    {
        deselectMouseAnnotation();
        if (show_crosshair)
        {
            mouse_current = Optional.empty();
            redraw();
        }
    }

    /** MouseTrackListener: {@inheritDoc} */
    @Override
    public void mouseHover(final MouseEvent e)
    {
        // NOP
    }

    /** Stagger the range of axes */
    public void stagger()
    {
        plot_processor.stagger();
    }

    /** Notify listeners */
    public void fireXAxisChange()
    {
        for (RTPlotListener<XTYPE> listener : listeners)
            listener.changedXAxis(x_axis);
    }

    /** Notify listeners */
    public void fireYAxisChange(final YAxisImpl<XTYPE> axis)
    {
        for (RTPlotListener<XTYPE> listener : listeners)
            listener.changedYAxis(axis);
    }

    /** Notify listeners */
    private void fireAnnotationsChanged()
    {
        for (RTPlotListener<XTYPE> listener : listeners)
            listener.changedAnnotations();
    }

    /** Notify listeners */
    private void fireCursorsChanged()
    {
        for (RTPlotListener<XTYPE> listener : listeners)
            listener.changedCursors();
    }

    /** Notify listeners */
    public void fireToolbarChange(final boolean show)
    {
        for (RTPlotListener<XTYPE> listener : listeners)
            listener.changedToolbar(show);
    }

    public void fireAutoScaleChange(YAxisImpl<XTYPE> axis)
    {
        for (RTPlotListener<XTYPE> listener : listeners)
            listener.changedAutoScale(axis);
    }
}
