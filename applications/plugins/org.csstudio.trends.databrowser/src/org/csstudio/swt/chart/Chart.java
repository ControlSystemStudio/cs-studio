package org.csstudio.swt.chart;

import java.util.ArrayList;

import org.csstudio.swt.chart.axes.Log10;
import org.csstudio.swt.chart.axes.TimeAxis;
import org.csstudio.swt.chart.axes.TraceNameYAxisFactory;
import org.csstudio.swt.chart.axes.TracePainter;
import org.csstudio.swt.chart.axes.XAxis;
import org.csstudio.swt.chart.axes.XAxisListener;
import org.csstudio.swt.chart.axes.YAxis;
import org.csstudio.swt.chart.axes.YAxisFactory;
import org.csstudio.swt.chart.axes.YAxisListener;
import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.swt.widgets.MessageBox;

/** Basic chart widget.
 * 
 *  @author Kay Kasemir
 */
public class Chart extends Canvas
{
    public static final boolean debug = false;
    
    // Note: InteractiveChart defines more style bits!
    public static final int USE_TRACE_NAMES = 1<<30;
    public static final int TIME_CHART = 1<<31;
    public static final int STYLE_MASK = ~(USE_TRACE_NAMES | TIME_CHART);
        
    /** The traces to plot. */
    private ArrayList<Trace> traces = new ArrayList<Trace>();
    
    /** Do we need to (re-)evaluate the overall layout of things? */
    private boolean dirty_layout = true;
    
    /** Overall size of this XYPlot */
    private Rectangle region;

    /** The actual plot/graph region, i.e. not the axes.. */
    private Rectangle plot_region;

    /** Horizontal axis. */
    private XAxis xaxis;
    private XAxisListener xaxis_listener;

    /** One or more vertical axes. */
    private YAxisFactory yaxis_factory;
    private ArrayList<YAxis> yaxes;
    private YAxisListener yaxis_listener;

    /** Background color. */
    private Color background;
    
    /** Background color. */
    private Color grid;
    
    /** Foreground color. */
    private Color foreground;

    /** Is mouse currently 'down'? */
    private boolean mouse_down = false;
    /** X coord for last mouse_down. */
    private int mouse_down_x;
    /** Y coord for last mouse_down. */
    private int mouse_down_y;
    /** Was mouse dragged while mouse_down? */
    private boolean mouse_dragged = false;
    
    private ArrayList<ChartListener> listeners = new ArrayList<ChartListener>();

    /** Create a chart.
     *  @param parent SWT parent widget.
     *  @param style SWT style (TIME_CHART, USE_TRACE_NAMES or 0).
     */
    public Chart(Composite parent, int style)
    {
        super(parent, style & STYLE_MASK);

        background = new Color(parent.getDisplay(), 255, 255, 255);
        grid = new Color(parent.getDisplay(), 150, 150, 150);
        foreground = new Color(parent.getDisplay(), 0, 0, 0);
        
        // Create axes
        xaxis_listener = new XAxisListener()
        {
            public void changedXAxis(XAxis xaxis)
            {
                if (debug)
                    System.out.println("Chart changedXAxis redraw"); //$NON-NLS-1$
                if (plot_region != null)
                {
                    // Compute redraw region for X axis and plot, not y axes
                    
                    	Rectangle xreg = xaxis.getRegion();
                    	redraw(plot_region.x+1, plot_region.y,
                    			plot_region.width-1,
                    			plot_region.height + xreg.height, true);
                    
                       
                }
                // Forward to ChartListeners
                for (ChartListener listener : listeners)
                    listener.changedXAxis(xaxis);
            }
        };
        // When a YAxis changes, redraw what's necessary
        // and inform chart listeners
        yaxis_listener = new YAxisListener()
        {
            /** Schedule redraw of axis itself. */
            @SuppressWarnings("nls")
            public void changedYAxis(int what, YAxis yaxis)
            {
                if (debug)
                    System.out.println("Chart changedYAxis redraw for "
                        + yaxis.getLabel() + ", type " + what);
                dirty_layout = true;
                if (plot_region != null)
                {   // Compute redraw region for Y axis (+ y axes to its right)
                    // and plot, not x axis.
                    Rectangle yreg = yaxis.getRegion();
                    redraw(yreg.x+1, yreg.y,
                           plot_region.x - yreg.x + plot_region.width-1,
                           plot_region.height-1, true);
                	
                }
                // Forward to chart listeners
                for (YAxisListener l : listeners)
                    l.changedYAxis(what, yaxis);
            }
    
        };
        if ((style & TIME_CHART) == TIME_CHART)
            xaxis = new TimeAxis(Messages.Chart_Time, xaxis_listener);
        else
            xaxis = new XAxis(Messages.Chart_x, xaxis_listener);
        
        if ((style & USE_TRACE_NAMES) == 0)
            yaxis_factory = new YAxisFactory();
        else
            yaxis_factory = new TraceNameYAxisFactory();
        yaxes = new ArrayList<YAxis>();
        YAxis yaxis = yaxis_factory.createYAxis(Messages.Chart_y, yaxis_listener);
        yaxes.add(yaxis);
        
        // Cleanup
        addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                for (Trace si : traces)
                    si.dispose();
                foreground.dispose();
                grid.dispose();
                background.dispose();
            }
        });
        
        // Adapt layout to resizes
        addControlListener(new ControlListener()
        {
            public void controlMoved(ControlEvent e) {}
            public void controlResized(ControlEvent e)
            {   dirty_layout = true;  }
        });
       
        // Allow this widget to draw itself
        addPaintListener(new PaintListener()
        {
            @SuppressWarnings("nls")
            public void paintControl(PaintEvent e)
            {   // All sorts of issues with the data model of
                // the application can result in errors while
                // we access the axis or sample info,
                // so this is a good place to catch them
                // and print debug info.
                try
                {
                    paint(e);  
                }
                catch (Throwable error)
                {
                    Plugin.logException("Chart paint error", error);
                }
            }
        });
        
        // Mouse-down: Mark a point, maybe start rubberband-zoom
        addMouseListener(new MouseListener()
        {
            public void mouseDoubleClick(MouseEvent event) {}

            public void mouseDown(MouseEvent event)
            {
                if (event.button != 1)
                    return;
                // Remember where the mouse went down
                mouse_down = true;
                mouse_dragged = false;
                mouse_down_x = event.x;
                mouse_down_y = event.y;
            }

            public void mouseUp(MouseEvent event)
            { 
                mouse_down = false;
                // If this was a 'drag', the rubberband zoom
                // already handled it.
                // Otherwise, it was a click. Handle it:
                if (mouse_dragged == false)
                    handleMouseClick(mouse_down_x, mouse_down_y);
            }    
        });
        
        // Mouse-move: rubber-band if button is down
        addMouseMoveListener(new MouseMoveListener()
        {
            private static final int MINIMUM = 10;
            public void mouseMove(MouseEvent e)
            {
                updateTooltip(e.x, e.y);
                if (! mouse_down)
                    return;
                int dx = e.x - mouse_down_x;
                if (Math.abs(dx) < MINIMUM)
                    return;
                int dy = e.y - mouse_down_y;
                if (Math.abs(dy) < MINIMUM)
                    return;
                mouse_dragged = true;
                // Turns out that on MacOS X at least,
                // the tracker can be 'invisible' when the
                // program has been moved between screens
                // since startup. Still 'works', but no rubberband...
                //Tracker tracker = new Tracker(Chart.this, SWT.RESIZE);
                // Keep rectangle normalized
                if (dx < 0)
                {
                    mouse_down_x = e.x;
                    dx = -dx;
                }
                if (dy < 0)
                {
                    mouse_down_y = e.y;
                    dy = -dy;
                }
                //tracker.setRectangles(new Rectangle[]
                //{ new Rectangle(mouse_down_x, mouse_down_y, dx, dy), });
                //if (tracker.open())
                //    rubberZoom(tracker.getRectangles()[0]);
                mouse_down = false;
            }
        });
        
        // We'll init an advanced cross hair selection tool
        new CrossHairSelectionTool();
        
    }
    
    /** Add a listener. */
    public void addListener(ChartListener listener)
    {
        listeners.add(listener);
    }

    /** Remove a listener */
    public void removeListener(ChartListener listener)
    {
        listeners.remove(listener);
    }
   
    /** @return Returns the x axis. */
    public XAxis getXAxis()
    {
        return xaxis;
    }
    
    /** @return Returns the number of y axes. */
    public int getNumYAxes()
    {
        return yaxes.size();
    }

    /** @return Returns required y axis.
     *  @see #getNumYAxes() */
    public YAxis getYAxis(int i)
    {
        return yaxes.get(i);
    }
    
    /** @return Returns index of the given Y-Axis or -1 if not found. */
    public int getYAxisIndex(YAxis yaxis)
    {
        return yaxes.indexOf(yaxis);
    }

    /** Add a new y axis with default label. */
    public YAxis addYAxis()
    {
        return addYAxis(Messages.Chart_y);
    }
    
    /** Add a new y axis with given label. */
    public YAxis addYAxis(String label)
    {
        YAxis yaxis = yaxis_factory.createYAxis(label, yaxis_listener);
        yaxes.add(yaxis);
        dirty_layout = true;
        redraw();
        return yaxis;
    }
    
    /** Remove the y axis of given index. */
    public void removeYAxis(int i)
    {
        YAxis yaxis = yaxes.remove(i);
        if (yaxis.getNumTraces() > 0)
            throw new Error("YAxis '" + yaxis.getLabel() //$NON-NLS-1$
                    + "' removed while there are still traces assigned to it"); //$NON-NLS-1$
        dirty_layout = true;
        redraw();
    }
    
    /** @returns Returns the selected y axis or <code>null</code>. */
    YAxis getSelectedYAxis()
    {
        for (YAxis yaxis : yaxes)
            if (yaxis.isSelected())
                return yaxis;
        return null;
    }

    /** @returns Returns the selected or first y axis. */
    YAxis getSelectedOrFirstYAxis()
    {
        for (YAxis yaxis : yaxes)
            if (yaxis.isSelected())
                return yaxis;
        return yaxes.get(0);
    }
    
    /** Get the Y-Axis at given screen coordinates.
     *  <p>
     *  This can be of use in Drag-and-Drop support, where the
     *  'drop' callback receives screen coordinates.
     *  Note that in most other cases, however, you have mouse coords
     *  relative to the current window, and then this method is <b>useless</b>.
     *  @param x Screen(!) X
     *  @param y Screen(!) Y
     *  @return The YAxis at that point or <code>null</code>.
     */
    public YAxis getYAxisAtScreenPoint(int x, int y)
    {
        // Transform event coordinates from display to widget
        Point drop_point = getDisplay().map(null, Chart.this, x, y);
        // Was a Y axis hit?
        for (YAxis yaxis : yaxes)
        {
            if (yaxis.getRegion().contains(drop_point))
                return yaxis;
        }
        return null;
    }
    
    /** @return Returns the number of traces. */
    public int getNumTraces()
    {
        return traces.size();
    }
    
    /** @return Returns the given trace.
     *  @see #getNumTraces()
     */
    public Trace getTrace(int index)
    {
        return traces.get(index);
    }
    
    /** Add a new trace to the chart.
     *  <p>
     *  The chart will initially redraw itself with the new trace.
     *  The chart will not, however, automatically redraw
     *  when the trace or the samples of the trace change!
     *  
     *  @param trace The trace to add
     *  @param autozoom When <code>true</code>, the chart will initially
     *                  auto-zoom the axis used by the trace.
     *                  Otherwise, those axis limits stay as they are.
     *  @return The newly added trace.
     *  @see #redrawTraces()
     */
    public Trace addTrace(String name, ChartSampleSequence series,
                    Color color, int line_width,
                    int yaxis_index, boolean autozoom, Trace.Type type)
    {
        YAxis yaxis = yaxes.get(yaxis_index);
        setRedraw(false);
        Trace trace = new Trace(name, series, color, line_width, yaxis, type, autozoom);
        traces.add(trace);
        if (autozoom)
            yaxis.autozoom(xaxis);
        setRedraw(true);
        return trace;
    } 
    
    /** Remove a trace.
     * 
     *  @param trace The trace to remove.
     */
    public void removeTrace(int index)
    {
        Trace trace = traces.remove(index);
        trace.dispose();
    }
    
    /** Toggle a redraw of the traces.
     *  <p>
     *  User code needs to invoke this routine after modifying
     *  a trace or its samples.
     */
    public void redrawTraces()
    {
        // The +-1 tweaks avoid the x and y axes, so only
        // the plot region gets redrawn.
        redraw(plot_region.x+1, plot_region.y,
               plot_region.width-1, plot_region.height-1,
               false);
    }
    
    public void redrawTracesWithBounds()
    {
    	redraw(plot_region.x, plot_region.y,
               plot_region.width, plot_region.height,
               false);
    }

    /** Auto-Zoom the selected or all Y axes. */
    public void autozoom(boolean checkTraceAutoscale)
    {
        YAxis yaxis = getSelectedYAxis();
        if (yaxis != null)
            yaxis.autozoom(xaxis, checkTraceAutoscale);
        else
        {
            // Defer redraw until all axes are adjusted...
            setRedraw(false);
            for (YAxis y : yaxes)
                y.autozoom(xaxis, checkTraceAutoscale);
            setRedraw(true);
        }
    }

    /** Auto-Zoom the selected or all Y axes. */
    public void stagger()
    {
        // Defer redraw until all axes are adjusted...
        setRedraw(false);
        // Determine range for each axis
        for (YAxis yaxis : yaxes)
            yaxis.autozoom(xaxis);
        // Now arrange them all so they don't overlap
        int N = yaxes.size();
        for (int i=0; i<N; ++i)
        {
            YAxis yaxis = yaxes.get(i);
            double low = yaxis.getLowValue();
            double high = yaxis.getHighValue();
            if (yaxis.isLogarithmic())
            {
                low = Log10.log10(low);
                high = Log10.log10(high);
            }
            double range = high - low;
            // Fudge factor to get some extra space
            range = 1.1*range;
            // Shift it down according to its index, using a total of N*range.
            low -= (N-i-1)*range;
            high += i*range;
            if (yaxis.isLogarithmic())
            {
                low = Log10.pow10(low);
                high = Log10.pow10(high);
            }
            yaxis.setValueRange(low, high);
        }
        setRedraw(true);
    }
    
    /** Provide size suggestion. Is ignored anyway. */
    @Override
    public Point computeSize(int wHint, int hHint, boolean changed)
    {
        return new Point(640, 320);
    }

    /** Perform the layout of axes, plot region, ...
     *  @param gc the GC to use for e.g. font info
     *  @param clientRect the area to use
     *  @see #dirty_layout
     */
    private void computeLayout(GC gc, Rectangle clientRect)
    {
        // For debugging, use an areas slightly smaller than the actual size
        if (debug)
        {
            System.out.println("computeLayout: " + clientRect.width + " x " + clientRect.height); //$NON-NLS-1$ //$NON-NLS-2$
            region = new Rectangle(50, 20, clientRect.width - 70, clientRect.height - 30);
        }
        else // otherwise, fill the given rect.
            region = new Rectangle(0, 0, clientRect.width, clientRect.height);

        // Compute the location of the plot/graph and the axes.
        //
        // Actual plot is a little inset to allow room
        // for labels on top of y and at the right end of x axes.
        // The -1, +1 compensates for the overlap of the axes at the
        // origin, so we use 100% of the overall region.
        int inset = 20;
        int xaxis_height = xaxis.getPixelHeight(gc);
        plot_region = new Rectangle(
                region.x - 1,
                region.y + inset,
                region.width - inset,
                region.height - inset - xaxis_height + 1);
        for (YAxis yaxis : yaxes)
        {
            int wid = yaxis.getPixelWidth(gc);
            plot_region.x += wid; 
            plot_region.width -= wid; 
        }
        
        // Axes are set to overlap at the lower-left origin
        // of the plot_region.
        // X axis is below the plot_region:
        xaxis.setRegion(plot_region.x,
                        plot_region.y + plot_region.height-1,
                        plot_region.width,
                        xaxis_height);
        // Y axes stagger to the left of the plot_region:
        int x = plot_region.x + 1;
        for (YAxis yaxis : yaxes)
        {
            int wid = yaxis.getPixelWidth(gc);
            x -= wid;
            yaxis.setRegion(x, plot_region.y,
                            wid, plot_region.height);
        }
        dirty_layout = false;
    }

    /** Paint almost everything, using the paint event's region
     *  to optimize a little bit.
     *  @see org.eclipse.swt.events.PaintListener
     */
    private void paint(PaintEvent e)
    {
        GC gc = e.gc;
        Rectangle r = getClientArea();
        if (dirty_layout)
            computeLayout(gc, r);
        // When used with gc.drawRectangle, this correction is needed to fit
        // 100%, since r.width is the actual width, while r.x+r.width would
        // be just one pixel beyond that:
        --r.width;
        --r.height;
        gc.setForeground(foreground);
        gc.setBackground(background);
        gc.fillRectangle(r);        
        gc.drawRectangle(r);

        // Paint axes
        // (which will decide to ignore the call based on the paint region)
        xaxis.paint(e);
        for (YAxis yaxis : yaxes)
            yaxis.paint(e);
        
        // The width-1 is a hack to _not_ redraw the plot
        // when only the Y axes needed a redraw.
        if (plot_region.intersects(e.x, e.y, e.width-1, e.height))
        {
            if (debug)
                System.out.println("paint the plot_region"); //$NON-NLS-1$
            // Clip just inside the X and Y axes.
            Rectangle clip = gc.getClipping();
            gc.setClipping(plot_region);
            // Grid, Traces, Markers
            paintGrid(gc);
            for (Trace t : traces)
                TracePainter.paint(gc, t, xaxis);
            // Restore what the TracePainter might have changed
            gc.setLineWidth(0);
            gc.setForeground(foreground);
            for (YAxis yaxis : yaxes)
                yaxis.paintMarkers(gc, xaxis);
            // Done
            gc.setClipping(clip);
        }
        // Show outline
        if (debug)
        {
            int dashes[] = { 5, 5 };
            gc.setLineDash(dashes);
            gc.drawRectangle(plot_region.x, plot_region.y, plot_region.width-1, plot_region.height-1);
            gc.drawRectangle(region.x, region.y, region.width-1, region.height-1);
            gc.setLineDash(null);
        }
    }

    /** Paint grid lines <u>if</u> there is a selected Y Axis. */
    private void paintGrid(GC gc)
    {
        YAxis yaxis = getSelectedYAxis();
        if (yaxis == null)
            return;
        gc.setForeground(grid);
        // Vertical grid lines for each X-Axis tick.
        ITicks xticks = xaxis.getTicks();
        int y0 = plot_region.y;
        int y1 = y0 + plot_region.height;
        for (double val = xticks.getStart();
            val <= xaxis.getHighValue();
            val = xticks.getNext(val))
        {
            int x = xaxis.getScreenCoord(val);
            gc.drawLine(x, y0, x, y1);
        }

        // Horizontal grid lines for each tick on the selected Y-Axis.
        ITicks yticks = yaxis.getTicks();
        int x0 = plot_region.x;
        int x1 = x0 + plot_region.width;
        for (double val = yticks.getStart();
            val <= yaxis.getHighValue();
            val = yticks.getNext(val))
        {
            int y = yaxis.getScreenCoord(val);
            gc.drawLine(x0, y, x1, y);
        }
        
        gc.setForeground(foreground);
    }
    
    /** Update tool-tip to something more or less useful for
     *  the given mouse coordinates.
     */
    private void updateTooltip(int mouse_x, int mouse_y)
    {
        if (plot_region.contains(mouse_x, mouse_y))
        {
            YAxis current = getSelectedOrFirstYAxis();
            double xc = xaxis.getValue(mouse_x);
            double yc = current.getValue(mouse_y);
            String tip = Messages.Chart_PointColon
                        + xaxis.getTicks().format(xc, 2)
                        + ", " //$NON-NLS-1$
                        + current.getTicks().format(yc, 2);
            setToolTipText(tip); 
            return;
        }
        // else: Is mouse over a Y axis?
        for (int i=0; i<yaxes.size(); ++i)
        {
            if (yaxes.get(i).getRegion().contains(mouse_x, mouse_y))
            {
                setToolTipText(Messages.Chart_SelectY_TT);
                return;
            }
        }
        // else:
        setToolTipText(Messages.Chart_DeselectY_TT);
    }

    /** Dispatch a mouse button 1 click: Select Y axis or point in plot. */
    private void handleMouseClick(int x, int y)
    {
        // Click in plot region -> get the value coordinates
        if (plot_region.contains(x, y))
        {
            YAxis current = getSelectedOrFirstYAxis();
            double xval = xaxis.getValue(x);
            double yval = current.getValue(y);
            // firePointSelected
            for (ChartListener listener : listeners)
                listener.pointSelected(xaxis, current, xval, yval);
            return;
        }
        // A click on a Y axis selects it, and de-selects the others.
        boolean any_selected = false;
        setRedraw(false);
        for (int i=0; i<yaxes.size(); ++i)
        {
            YAxis yaxis = yaxes.get(i);
            boolean selected = yaxis.getRegion().contains(x, y);
            yaxes.get(i).setSelected(selected);
            
            org.csstudio.swt.chart.Trace trace = yaxes.get(i).getTraceAt(x, y);
            if(trace != null)
            	Plugin.logInfo("Trace " + trace.getName() + " selected.");            	
            
            if (selected)
                any_selected = true;
        }
        // redraw the plot, since the grid might have changed
        setRedraw(true);
        
        if (any_selected)
            return;
        // fireNothingSelected
        for (ChartListener listener : listeners)
            listener.nothingSelected();
    }
    
    /** Handle a rubber-band-zoom for the given region. */
    private void rubberZoom(Rectangle zoom)
    {
        setRedraw(false); // defer per-axis redraws
        boolean selections = false;
        // Zoom only the selected axes?
        for (YAxis yaxis : yaxes)
        {
            if (! yaxis.isSelected())
                continue;
            selections = true;
            yaxis.setValueRange(
                    yaxis.getValue(zoom.y + zoom.height -1),
                    yaxis.getValue(zoom.y));
        }
        if (!selections) // Nothing selected, zoom all y axes
            for (YAxis yaxis : yaxes)
                yaxis.setValueRange(
                        yaxis.getValue(zoom.y + zoom.height -1),
                        yaxis.getValue(zoom.y));
        // Zoom x axis
        xaxis.setValueRange(
                xaxis.getValue(zoom.x),
                xaxis.getValue(zoom.x + zoom.width - 1));
        setRedraw(true); // now redraw all
    }
    
    public int showMessage(String title, String message, int style)
    {
    	MessageBox msgDialog = new MessageBox(this.getShell(), style);
    	msgDialog.setMessage(message);
    	msgDialog.setText(title);
    	return msgDialog.open();
    }

    /**
     * A simple class which uses xor to draw selection. It is fast and optimized
     * to display selected region without flickering. However this implementation
     * won't work on MacOS, but it is a nice feature for Windows and Linux users.
     *  
     * @author blipusce
     *
     */
    private class CrossHairSelectionTool
    {
    	private final Cursor cursorCross;
    	private final Cursor cursorDefault;
    	
    	private final Point EmptyPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
    	private final Rectangle EmptyRect = new Rectangle(0, 0, 0, 0);
    	private final Color selectionColor;
    	
    	private Rectangle prevZoomRect = EmptyRect;
    	private Point mouseLocation = EmptyPoint;
    	private Point mousePoint = EmptyPoint;
    	
    	public CrossHairSelectionTool() 
    	{   
    		this.selectionColor = new Color(Chart.this.getDisplay(), 57, 41, 16);
    		this.cursorCross = new Cursor(getDisplay(), SWT.CURSOR_CROSS);
    		this.cursorDefault = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
    		
    		addMouseListener(new MouseListener() 
    		{
    			public void mouseDown(MouseEvent e)
    			{
    				if(plot_region.contains(e.x, e.y)) 
    				{
    					mousePoint = new Point(e.x, e.y);
    					Chart.this.redrawTracesWithBounds();
    				}
    			}
    			
    			public void mouseUp(MouseEvent e)
    			{
    				rubberZoom(getZoomRect());
    				mousePoint = EmptyPoint;
    				prevZoomRect = EmptyRect;
    				Chart.this.redrawTracesWithBounds();
    			}

				public void mouseDoubleClick(MouseEvent e) {}
    		});
    		
    		addMouseTrackListener(new MouseTrackListener() 
    		{
    			public void mouseEnter(MouseEvent e) { mouseLocation = EmptyPoint; } 
    			
    			public void mouseExit(MouseEvent e)
    			{ 
    				mouseLocation = EmptyPoint;
    				Chart.this.redrawTracesWithBounds();
    			} 
    			
    			public void mouseHover(MouseEvent e) {}
    		});
    		
    		addMouseMoveListener(new MouseMoveListener() {
    			
    			public void mouseMove(MouseEvent e) 
    	    	{
    	    		if(plot_region.contains(e.x, e.y)) 
    	    		{
    	    			if(mouseLocation == EmptyPoint)
    	    			{
    	    				prevZoomRect = EmptyRect;
    	    				redrawTraces();
    	    			}
    	    			
    	    			setCursor(cursorCross);
    	    			// Let's paint it.
    	    			GC gc = new GC(Chart.this);
    	    			// First lets erase previous crosshair trace.
    	    			paintCrossHair(gc, false);
    	    			// Set new location.
    	    			mouseLocation = new Point(e.x, e.y);
    	    			// Redraw again.
    	    			paintCrossHair(gc, false);
    	    			// Let's clear resources.
    	    			gc.dispose();
    	    		}
    	    		else {
    	    			if(mouseLocation.x != Integer.MIN_VALUE || mouseLocation.y != Integer.MIN_VALUE)
    	    			{
    	    				mouseLocation = EmptyPoint;
    	    				prevZoomRect = EmptyRect;
    	    				Chart.this.redrawTracesWithBounds();
    	    			}
    	    			setCursor(cursorDefault);
    	    		}
    	    	}
    		});
    		
    		addPaintListener(new PaintListener() {
    			public void paintControl(PaintEvent e) { paintCrossHair(e.gc, true); }
    		});
    	}
    	
    	public void paintCrossHair(GC gc, boolean completePaint) {
			try {
				if (mouseLocation == EmptyPoint)
					return;

				// Lets get xor color.
				gc.setForeground(Chart.this.getBackground());

				// We enter xor mode.
				gc.setXORMode(true);
				gc.setLineStyle(SWT.LINE_DOT);

				// Draw vertical and horizontal position lines.
				if (mouseLocation.x != mousePoint.x)
					gc.drawLine(mouseLocation.x, plot_region.y,
							mouseLocation.x, plot_region.y + plot_region.height
									- 2);
				if (mouseLocation.y != mousePoint.y)
					gc.drawLine(plot_region.x + 1, mouseLocation.y,
							plot_region.x + plot_region.width - 2,
							mouseLocation.y);

				if (mousePoint != EmptyPoint) {
					if (completePaint) {
						// Draw selected start position lines.
						gc.drawLine(mousePoint.x, plot_region.y, mousePoint.x,
								plot_region.y + plot_region.height - 2);
						gc.drawLine(plot_region.x + 1, mousePoint.y,
								plot_region.x + plot_region.width - 2,
								mousePoint.y);
					}

					// Draw selection.
					gc.setBackground(selectionColor);
					Rectangle zoomRect = getZoomRect();

					if (!completePaint) {
						Region regIntersection = new Region();
						Region regRepaint = new Region();

						// Lets get tha part we'll not repaint.
						regIntersection.add(prevZoomRect);
						regIntersection.intersect(zoomRect);

						// Now calculate repaint region.
						regRepaint.add(zoomRect);
						regRepaint.add(prevZoomRect);
						regRepaint.subtract(regIntersection);

						// Get repaint area union.
						Rectangle redrawRect = new Rectangle(prevZoomRect.x,
								prevZoomRect.y, prevZoomRect.width,
								prevZoomRect.height);
						redrawRect.add(zoomRect);

						// Repaint with clipping.
						Rectangle rectClip = gc.getClipping();
						gc.setClipping(regRepaint);
						gc.fillRectangle(Chart.this.plot_region);
						gc.setClipping(rectClip);

						// Set new rectangle.
						prevZoomRect = zoomRect;

						// Dispose resources.
						regIntersection.dispose();
						regRepaint.dispose();

					} else {
						// It is a simple repaint, just paint zoom rectangle.
						gc.fillRectangle(zoomRect);
					}
				}
				// Reset xor mode.
				gc.setXORMode(false);
			} catch (Exception e) {
				// Just catch any exceptions.
			}
		}
    	
    	private Rectangle getZoomRect() 
    	{
    		return getZoomRect(mousePoint, mouseLocation);
    	}
    	
    	private Rectangle getZoomRect(Point p1, Point p2) {
    		
    		if(p1 == EmptyPoint || p2  == EmptyPoint) 
    			return EmptyRect;
    		
    		int x = Math.min(p1.x, p2.x) + 1;
			int y = Math.min(p1.y, p2.y) + 1;
			int width = Math.max(0, Math.abs(p1.x - p2.x) - 1);
			int height = Math.max(0, Math.abs(p1.y - p2.y) - 1);
			
			return new Rectangle(x, y, width, height);
    	}
    }
}
