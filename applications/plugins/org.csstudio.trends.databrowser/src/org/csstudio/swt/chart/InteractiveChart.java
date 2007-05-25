package org.csstudio.swt.chart;

import org.csstudio.swt.chart.axes.Axis;
import org.csstudio.swt.chart.axes.Log10;
import org.csstudio.swt.chart.axes.TraceSample;
import org.csstudio.swt.chart.axes.XAxis;
import org.csstudio.swt.chart.axes.YAxis;
import org.csstudio.swt.chart.axes.YAxisListener;
import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/** Basic chart with buttons for zoom/pan.
 *  <p>
 *  To use, one would probably mostly deal with the embedded chart
 *  via getChart().
 *  <p>
 *  In addition, one might want to override setDefaultRanges()
 *  to define what that means.
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
       
    /** Widget constructor.
     *  @param parent Parent widget.
     *  @param style SWT style.
     */
    @SuppressWarnings("nls")
    public InteractiveChart(Composite parent, int style)
    {
        super(parent, style & Chart.STYLE_MASK);
        zoom_from_end = (style & ZOOM_X_FROM_END) != 0;
        initButtonImages();
        
        makeGUI(style);
        
        chart.addListener(new ChartListener()
        {
            public void changedXAxis(XAxis xaxis)
            {}
            
            public void changedYAxis(YAxisListener.Aspect what, YAxis yaxis)
            {}

            public void pointSelected(XAxis xaxis, YAxis yaxis, double x, double y)
            {   // Adds a marker for the selected sample
                if (yaxis == null)
                    return;
                TraceSample best = yaxis.getClosestSample(xaxis, x, y);
                if (best != null)
                {
                    ChartSample sample = best.getSample();
                    x = sample.getX();
                    y = sample.getY();
                    StringBuffer b = new StringBuffer();
                    b.append(best.getTrace().getName());
                    b.append("\n"); //$NON-NLS-1$
                    b.append(xaxis.getTicks().format(x, 2));
                    // Show value, unless it's NaN or +- inf
                    if (!Double.isInfinite(y) &&
                        !Double.isNaN(y))
                    {
                    b.append("\n"); //$NON-NLS-1$
                    b.append(yaxis.getTicks().format(y, 2));
                    }
                    if (sample.getInfo() != null)
                    {
                        b.append("\n"); //$NON-NLS-1$
                        b.append(sample.getInfo());
                    }
                    yaxis.addMarker(x, y, b.toString());
                }
            }
        });
    }

    /** Initialize image registry for button images */
    @SuppressWarnings("nls")
    private void initButtonImages()
    {
        if (button_images != null)
            return;
        button_images = new ImageRegistry();
        try
        {
            button_images.put(UP, Plugin.getImageDescriptor("icons/up.gif"));
            button_images.put(DOWN, Plugin.getImageDescriptor("icons/down.gif"));
            button_images.put(Y_IN, Plugin.getImageDescriptor("icons/y_in.gif"));
            button_images.put(Y_OUT, Plugin.getImageDescriptor("icons/y_out.gif"));
            button_images.put(ZOOM, Plugin.getImageDescriptor("icons/autozoom.gif"));
            button_images.put(STAGGER, Plugin.getImageDescriptor("icons/stagger.gif"));
            button_images.put(LEFT, Plugin.getImageDescriptor("icons/left.gif"));
            button_images.put(RIGHT, Plugin.getImageDescriptor("icons/right.gif"));
            button_images.put(X_IN, Plugin.getImageDescriptor("icons/x_in.gif"));
            button_images.put(X_OUT, Plugin.getImageDescriptor("icons/x_out.gif"));
            button_images.put(DEFAULT_ZOOM, Plugin.getImageDescriptor("icons/defaultscale.gif"));
        }
        catch (Exception e)
        {
            Plugin.logException("InteractiveChart cannot init. images", e);
        }
    }

    /** Add all the button bar buttons. */
    private void addButtons()
    {
        addButton(UP, Messages.Chart_MoveUp, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                moveUpDown(SHIFT_FACTOR);
            }
        });
        addButton(DOWN, Messages.Chart_MoveDown, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                moveUpDown(-SHIFT_FACTOR);
            }
        });
        addButton(Y_IN, Messages.Chart_ZoomIn, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                zoomInOutY(ZOOM_FACTOR);
            }
        });
        addButton(Y_OUT, Messages.Chart_ZoomOut, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                zoomInOutY(1.0/ZOOM_FACTOR);
            }
        });
        addButton(ZOOM, Messages.Chart_ZoomAuto, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                chart.autozoom();
            }
        });
        addButton(STAGGER, Messages.Chart_Stagger, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                chart.stagger();
            }
        });
        addButton(DEFAULT_ZOOM, Messages.Chart_Default_Scale, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                chart.setDefaultZoom();
            }
        });
        
        // Unclear: This right now pans the _graph_
        // left/right. Should it move the _curves_?
        addButton(LEFT, Messages.Chart_MoveLeft, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                moveLeftRight(PAN_FACTOR);
            }
        });
        addButton(RIGHT, Messages.Chart_MoveRight, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                moveLeftRight(-PAN_FACTOR);
            }
        });
        addButton(X_IN, Messages.Chart_TimeIn, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (zoom_from_end)
                    zoomFromEnd(chart.getXAxis(), ZOOM_FACTOR);
                else
                    zoomInOut(chart.getXAxis(), ZOOM_FACTOR, false);
            }
        });
        addButton(X_OUT, Messages.Chart_TimeOut, new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
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
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        setLayout(gl);
        GridData gd;
        
        button_bar = new Composite(this, 0);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        button_bar.setLayoutData(gd);
        button_bar.setLayout(new RowLayout());
        addButtons();
        
        // X/Y Axes
        chart = new Chart(this, style);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        chart.setLayoutData(gd);
    }

    /** @return Returns the axes. */
    public Chart getChart()
    {
        return chart;
    }
    
    /** The user can add buttons or other widgets to this button bar.
     *  <p>
     *  The button bar uses a horizontal RowLayout, so newly added buttons
     *  go to the _end_ of the bar.
     *  TODO: Allow new buttons anywhere in the bar, not just the end?
     *  @return Returns the button bar. Use as the 'parent' for new buttons.
     */
    public Composite getButtonBar()
    {
        return button_bar;
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
} 
