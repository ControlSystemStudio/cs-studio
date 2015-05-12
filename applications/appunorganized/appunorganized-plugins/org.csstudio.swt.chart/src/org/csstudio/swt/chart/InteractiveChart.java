/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

import java.util.ArrayList;

import org.csstudio.swt.chart.axes.Axis;
import org.csstudio.swt.chart.axes.Log10;
import org.csstudio.swt.chart.axes.Marker;
import org.csstudio.swt.chart.axes.TraceSample;
import org.csstudio.swt.chart.axes.XAxis;
import org.csstudio.swt.chart.axes.YAxis;
import org.csstudio.swt.chart.axes.YAxisListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/** Basic chart with buttons for zoom/pan.
 *  <p>
 *  To use, one would probably mostly deal with the embedded chart
 *  via getChart().
 *
 *  @author Kay Kasemir
 */
public class InteractiveChart extends Composite
{
    // Note: this contant must jive with Chart.STYLE_MASK
    /** Flag to select X axis zoom to keep the right edge (end) of the plot */
    public static final int ZOOM_X_FROM_END = 1<<29;
    public static final int STYLE_MASK = ~(ZOOM_X_FROM_END | Chart.STYLE_MASK);

    /** Amount to pan left/right */
    private static final double PAN_FACTOR = 5.0;
    /** Amount to shift up/down */
    private static final double SHIFT_FACTOR = 9.0;
    /** Amount to zoom in/out */
    private static final double ZOOM_FACTOR = 1.5;

    private Chart chart;
    private Composite button_bar;
    /** In principle, the button_bar.isVisible() method tells us
     *  if it's visible.
     *  Except right at startup the button bar is in principle configured
     *  to be visible, but it's not, yet, and that messes everything up,
     *  so we keep track ourself.
     */
    private boolean button_bar_visible = true;

    private static ImageRegistry button_images = null;
    private static final String UP = "up"; //$NON-NLS-1$
    private static final String DOWN = "down"; //$NON-NLS-1$
    private static final String Y_IN = "y_in"; //$NON-NLS-1$
    private static final String Y_OUT = "y_out"; //$NON-NLS-1$
    private static final String ZOOM = "autozoom"; //$NON-NLS-1$
    private static final String STAGGER = "stagger"; //$NON-NLS-1$
    private static final String LEFT = "left"; //$NON-NLS-1$
    private static final String RIGHT = "right"; //$NON-NLS-1$
    private static final String X_IN = "x_in"; //$NON-NLS-1$
    private static final String X_OUT = "x_out"; //$NON-NLS-1$
    private static final String DEFAULT_ZOOM = "defaultscale"; //$NON-NLS-1$

    /** Was ZOOM_X_FROM_END set? */
    private boolean zoom_from_end;

    private ArrayList<InteractiveChartListener> listeners =
        new ArrayList<InteractiveChartListener>();

    /** Widget constructor.
     *  @param parent Parent widget.
     *  @param style SWT style.
     */
    public InteractiveChart(Composite parent, int style)
    {
        super(parent, style & Chart.STYLE_MASK);
        zoom_from_end = (style & ZOOM_X_FROM_END) != 0;
        initButtonImages();

        makeGUI(style);

        chart.addListener(new ChartListener()
        {
            @Override
            public void aboutToZoomOrPan(String description)
            { /* NOP */ }

            @Override
            public void changedXAxis(XAxis xaxis)
            { /* NOP */ }

            @Override
            public void changedYAxis(YAxisListener.Aspect what, YAxis yaxis)
            { /* NOP */ }

            @Override
            public void pointSelected(final int x, final int y)
            {
                addMarker(x, y);
            }
        });
    }

    /** Add a listener */
    public void addListener(InteractiveChartListener listener)
    {
        listeners.add(listener);
    }

    /** Remove a listener */
    public void removeListener(InteractiveChartListener listener)
    {
        listeners.remove(listener);
    }

    /** Initialize image registry for button images */
    @SuppressWarnings("nls")
    private void initButtonImages()
    {
        if (button_images != null)
            return;
        button_images = new ImageRegistry();
        addButtonImage(UP, "icons/up.gif");
        addButtonImage(DOWN, "icons/down.gif");
        addButtonImage(Y_IN, "icons/y_in.gif");
        addButtonImage(Y_OUT, "icons/y_out.gif");
        addButtonImage(ZOOM, "icons/autozoom.gif");
        addButtonImage(STAGGER, "icons/stagger.gif");
        addButtonImage(LEFT, "icons/left.gif");
        addButtonImage(RIGHT, "icons/right.gif");
        addButtonImage(X_IN, "icons/x_in.gif");
        addButtonImage(X_OUT, "icons/x_out.gif");
        addButtonImage(DEFAULT_ZOOM, "icons/defaultscale.gif");
    }

    /** Add image with given file name to registry under given id */
    private void addButtonImage(final String id, final String filename)
    {
        final ImageDescriptor image = Activator.getImageDescriptor(filename);
        if (image != null)
            button_images.put(id, image);
    }

    /** Add all the button bar buttons. */
    private void addButtons()
    {
        // The button bar uses Row Layout
        button_bar.setLayout(new RowLayout());
        addButton(UP, Messages.Chart_MoveUp, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_MoveUp);
                moveUpDown(SHIFT_FACTOR);
            }
        });
        addButton(DOWN, Messages.Chart_MoveDown, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_MoveDown);
                moveUpDown(-SHIFT_FACTOR);
            }
        });
        addButton(Y_IN, Messages.Chart_ZoomIn, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_ZoomIn);
                zoomInOutY(ZOOM_FACTOR);
            }
        });
        addButton(Y_OUT, Messages.Chart_ZoomOut, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_ZoomOut);
                zoomInOutY(1.0/ZOOM_FACTOR);
            }
        });
        addButton(ZOOM, Messages.Chart_ZoomAuto, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_ZoomAuto);
                chart.autozoom();
            }
        });
        addButton(STAGGER, Messages.Chart_Stagger, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_Stagger);
                chart.stagger();
            }
        });
        addButton(DEFAULT_ZOOM, Messages.Chart_Default_Scale, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_Default_Scale);
                chart.setDefaultZoom();
            }
        });

        // Unclear: This right now pans the _graph_
        // left/right. Should it move the _curves_?
        addButton(LEFT, Messages.Chart_MoveLeft, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_MoveLeft);
                moveLeftRight(PAN_FACTOR);
            }
        });
        addButton(RIGHT, Messages.Chart_MoveRight, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_MoveRight);
                moveLeftRight(-PAN_FACTOR);
            }
        });
        addButton(X_IN, Messages.Chart_TimeIn, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_TimeIn);
                if (zoom_from_end)
                    zoomFromEnd(chart.getXAxis(), ZOOM_FACTOR);
                else
                    zoomInOut(chart.getXAxis(), ZOOM_FACTOR, false);
            }
        });
        addButton(X_OUT, Messages.Chart_TimeOut, new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                chart.fireAboutToZoomOrPan(Messages.Chart_TimeOut);
                if (zoom_from_end)
                    zoomFromEnd(chart.getXAxis(), 1.0/ZOOM_FACTOR);
                else
                    zoomInOut(chart.getXAxis(), 1.0/ZOOM_FACTOR, false);
            }
        });
    }

    private void makeGUI(int style)
    {
        // Top:  Button Bar
        // Rest: Plot
        FormLayout layout = new FormLayout();
        setLayout(layout);
        FormData fd;

        button_bar = new Composite(this, 0);
        fd = new FormData();
        fd.top = new FormAttachment(0, 0);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(100, 0);
        button_bar.setLayoutData(fd);

        addButtons();

        // X/Y Axes
        chart = new Chart(this, style);
        fd = new FormData();
        fd.top = new FormAttachment(button_bar);
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(100, 0);
        fd.bottom = new FormAttachment(100, 0);
        chart.setLayoutData(fd);
    }

    /** The user can add buttons or other widgets to this button bar.
     *  <p>
     *  The button bar uses a horizontal RowLayout, so newly added buttons
     *  go to the <u>end</u> of the bar.
     *  @return Returns the button bar. Use as the 'parent' for new buttons.
     */
    public Composite getButtonBar()
    {
        return button_bar;
    }

    /** @return <code>true</code> if the button bar is enabled to be visible */
    public boolean isButtonBarVisible()
    {
        return button_bar_visible;
    }

    /** Show (or hide) the button bar
     *  @param show <code>true</code> to show the button bar.
     */
    public void showButtonBar(boolean show)
    {
        if (show)
        {
            if (button_bar_visible)
                return; // NOP
            // Hook plot's top to the button bar
            FormData fd = (FormData)chart.getLayoutData();
            fd.top = new FormAttachment(button_bar);
            chart.setLayoutData(fd);
            // Make button bar visible
            button_bar_visible = true;
        }
        else
        {   // hide button bar
            if (button_bar_visible == false)
                return; // NOP
            // Hook plot's top to the top of the window
            FormData fd = (FormData)chart.getLayoutData();
            fd.top = new FormAttachment(0, 0);
            chart.setLayoutData(fd);
            // Hide button bar
            button_bar_visible = false;
        }
        button_bar.setVisible(button_bar_visible);
        // Notify listeners
        for (InteractiveChartListener listener : listeners)
            listener.buttonBarChanged(button_bar_visible);
        // Ask shell to re-evaluate the changed layout
        chart.getShell().layout(true, true);
    }

    /** @return Returns the axes. */
    public Chart getChart()
    {
        return chart;
    }

    /** Add a button to the button bar.
     *
     *  @param image_key Image file name in icons subdir.
     *  @param tooltip Tooltip text.
     *  @param sel Selection Listener to invoke on button press.
     */
    private void addButton(String image_key, String tooltip, SelectionListener sel)
    {
        Button btn = new Button(button_bar, SWT.CENTER);
        Image image = button_images.get(image_key);
        if (image != null)
            btn.setImage(image);
        else
            btn.setText(image_key);
        btn.setToolTipText(tooltip);
        btn.addSelectionListener(sel);
    }

    /** Move the selected or all Y axes up/down.
     *  @param fraction Positive values move 'up'. */
    private void moveUpDown(double fraction)
    {
        YAxis yaxis = chart.getSelectedYAxis();
        if (yaxis != null)
        {   // skip axis that takes care of itself
            if (yaxis.getAutoScale())
                return;
            move(yaxis, fraction, yaxis.isLogarithmic());
        }
        else
            for (int i=0; i<chart.getNumYAxes(); ++i)
            {
                yaxis = chart.getYAxis(i);
                // skip axis that takes care of itself
                if (yaxis.getAutoScale())
                    continue;
                move(yaxis, fraction, yaxis.isLogarithmic());
            }
    }

    /** Move the X axis left/right. */
    private void moveLeftRight(double fraction)
    {
        move(chart.getXAxis(), fraction, false);
    }

    /** Move axis up or down
     *
     * @param axis Axis to move
     * @param fraction "4" will move values 1/4 of current range up.
     */
    @SuppressWarnings("nls")
    private void move(Axis axis, double fraction, boolean use_log)
    {
        if (Chart.debug)
            System.out.println("Move '" + axis.getLabel() + "' by " + fraction);
        double low = axis.getLowValue();
        double high = axis.getHighValue();
        if (use_log)
        {
            low = Log10.log10(low);
            high = Log10.log10(high);
        }
        double shift = (high - low)/fraction;
        low -= shift;
        high -= shift;
        if (use_log)
        {
            low = Log10.pow10(low);
            high = Log10.pow10(high);
        }
        axis.setValueRange(low, high);
    }

    /** Zoom the selected or all Y axes */
    private void zoomInOutY(double factor)
    {
        YAxis yaxis = chart.getSelectedYAxis();
        if (yaxis != null)
            zoomInOut(yaxis, factor, yaxis.isLogarithmic());
        else
            for (int i=0; i<chart.getNumYAxes(); ++i)
            {
                yaxis = chart.getYAxis(i);
                zoomInOut(yaxis, factor, yaxis.isLogarithmic());
            }
    }

    /** Zoom in or out
     *
     *  @param axis Axis to zoom
     *  @param factor zoom factor.
     *  @param use_log Zoom linearly or in logarithmic value space?
     */
    @SuppressWarnings("nls")
    private void zoomInOut(Axis axis, double factor, boolean use_log)
    {
        double low = axis.getLowValue();
        double high = axis.getHighValue();
        if (Chart.debug)
            System.out.println("Zoom '" + axis.getLabel()
                               + "' by " + factor
                               + (use_log ? " (log.)" : " (linearly)"));
        if (use_log)
        {
            low = Log10.log10(low);
            high = Log10.log10(high);
        }
        double center = (high + low)/2.0;
        double zoomed = (high - low) / factor / 2.0;
        low = center - zoomed;
        high = center + zoomed;
        if (use_log)
        {
            low = Log10.pow10(low);
            high = Log10.pow10(high);
        }
        // Don't update range if the result would be garbage.
        if (Double.isInfinite(low) || Double.isInfinite(high))
            return;
        axis.setValueRange(low, high);
    }

    /** Zoom in or out
     *
     *  @param axis Axis to zoom
     *  @param factor zoom factor.
     */
    @SuppressWarnings("nls")
    private void zoomFromEnd(Axis axis, double factor)
    {
        if (Chart.debug)
            System.out.println("Zoom '" + axis.getLabel() + "' by " + factor + " from end");
        double low = axis.getLowValue();
        double high = axis.getHighValue();
        double range = high - low;
        axis.setValueRange(high - range/factor, high);
    }

    /** Add a marker for the current screen coord.
     *  @param sx,sy X/Y coords in screen coords
     */
    private void addMarker(final int sx, final int sy)
    {
        // Get value space x0/y0 for screen coords sx, sy ...
        final XAxis xaxis = chart.getXAxis();
        final double x0 = xaxis.getValue(sx);
        // ... using which Y axis?
        TraceSample best_sample = null;
        YAxis best_axis = chart.getSelectedYAxis();
        if (best_axis != null)
        {   // Use selected axis
            final double y0 = best_axis.getValue(sy);
            best_sample = best_axis.getClosestSample(xaxis, x0, y0);
        }
        else
        {   // Locate Y Axis with closest sample
            long closest = Long.MAX_VALUE;
            for (int i=0;  i<chart.getNumYAxes(); ++i)
            {   // Get nearest sample on this yaxis
                final YAxis yaxis = chart.getYAxis(i);
                final double y0 = yaxis.getValue(sy);
                final TraceSample near = yaxis.getClosestSample(xaxis, x0, y0);
                if (near == null)
                    continue;
                // Is that the best match to sx/sy?
                final int x = xaxis.getScreenCoord(near.getSample().getX());
                final int y = yaxis.getScreenCoord(near.getSample().getY());
                final int dx = sx-x, dy = sy-y;
                final long dist = dx*dx + dy*dy;
                if (dist < closest)
                {
                    closest = dist;
                    best_sample = near;
                    best_axis = yaxis;
                }
            }
        }
        if (best_sample == null)
            return;

        // Add marker for the selected sample
        final ChartSample sample = best_sample.getSample();
        final double x = sample.getX(),  y = sample.getY();
        final StringBuilder b = new StringBuilder();
        b.append(best_sample.getTrace().getName());
        b.append("\n"); //$NON-NLS-1$
        b.append(xaxis.getTicks().format(x, 2));
        // Show value, unless it's NaN or +- inf
        if (! (Double.isInfinite(y) || Double.isNaN(y)))
        {
            b.append("\n"); //$NON-NLS-1$
            b.append(best_axis.getTicks().format(y, 2));
        }
        if (sample.getInfo() != null)
        {
            b.append("\n"); //$NON-NLS-1$
            b.append(sample.getInfo());
        }
        best_axis.addMarker(new Marker(x, y, b.toString()));
    }
}
