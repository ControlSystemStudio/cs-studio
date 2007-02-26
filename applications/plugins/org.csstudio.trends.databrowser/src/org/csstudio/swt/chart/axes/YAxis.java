package org.csstudio.swt.chart.axes;

import java.util.ArrayList;

import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSearch;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Trace;
import org.csstudio.util.swt.GraphicsUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/** A 'Y' or 'vertical' axis.
 *  <p>
 *  The chart maintains one or more Y axes.
 *  Each trace to plot needs to be assigned to a Y axis.
 *  
 *  @see Trace
 *  @see Chart
 *  @author Kay Kasemir
 */
public class YAxis extends Axis
{
    /** The traces to plot. */
    private ArrayList<Trace> traces = new ArrayList<Trace>();
    
    /** Markers.
     *  <p>
     *  One could argue if markers belong to the Graph, YAxis or Trace.
     *  If on a trace, they should probably be on a single sample.
     *  Usually, that's what we actually want.
     *  But maybe we'll later also want to mark a location that's
     *  between samples or between traces?
     *  So then it must be on an axis or global to the graph.
     *  For the latter, it's unclear where to exactly paint it,
     *  so it's for now on the y-axis.
     */
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    
    /** The listener. For now exactly one, must not be <code>null</code>. */ 
    private YAxisListener listener;
    
    /** <code>true</code> if this axis is selected. 
     *  The chart actually only deals with one selected axis at a time,
     *  but maybe later one can select multiple axes? */
    private boolean selected;
    
    /** Set <code>true</code> to get minor tick marks. */
    private boolean show_minor_ticks = true;
    
    /** Construct a new Y axis.
     *  <p>
     *  Note that end users will typically <b>not</b> create new Y axes,
     *  but instead ask the <code>Chart</code> for a new or existing axis.
     *  <p>
     *  @param label The (initial) axis label.
     *  @param listener Listener.
     */
    public YAxis(String label, YAxisListener listener)
    {
        super(false, label, new Ticks(), new LinearTransform());
        this.listener = listener;
    }

    /** Set a new label. */
    public final void setLabel(String new_label)
    {
        label = new_label;
        fireEvent(YAxisListener.LABEL);
    }
    
    /** Configure the Y-Axis to use a log. scale or not.
     *  <p>
     *  Initial default is <code>false</code>, i.e. linear scale.
     */ 
    public final void setLogarithmic(boolean use_log)
    { 
        ticks = (use_log ? new LogTicks() : new Ticks());
        transform = (use_log ? new LogTransform() : new LinearTransform());
        // re-initialize the transform; set 'dirty' ticks
        setValueRange(low_value, high_value);
    }
    
    /** @return <code>true</code> if the axis is logarithmic. */
    public boolean isLogarithmic()
    {
        return ticks instanceof LogTicks;
    }

    /** Fire given event. */
    protected final void fireEvent(int what)
    {   // Silly, but maybe there'll once be a list of listeners?
        listener.changedYAxis(what, this);
    }
    
    /** Add a trace to the axis.
     *  <p>
     *  Not meant for end users, the trace is supposed to add itself.
     */
    public void addTrace(Trace trace)
    {
        if (traces.contains(trace))
            throw new Error(
               "Internal YAxis error, Trace is added multiple times"); //$NON-NLS-1$
        traces.add(trace);
    }

    /** Remove a trace from the axis.
     *  <p>
     *  Not meant for end users, the trace is supposed to remove itself.
     */
    public void removeTrace(Trace trace)
    {
        if (!traces.contains(trace))
            throw new Error("Internal YAxis error, unknown trace"); //$NON-NLS-1$
        traces.remove(trace);
    }
    
    /** @return Returns the number of traces. */
    public final int getNumTraces()
    {
        return traces.size();
    }
    
    /** @return Returns the trace of given index. */
    public final Trace getTrace(int index)
    {
        return traces.get(index);
    }

    /** Add a marker.
     *  @param position The 'x' position.
     *  @param value The 'y' location or value.
     *  @param text The text to display
     */
    public final void addMarker(double position, double value, String text)
    {
        markers.add(new Marker(position, value, text));
        fireEvent(YAxisListener.MARKER);
    }

    /** Remove all markers from this axis. */
    public final void removeMarkers()
    {
        markers.clear();
        fireEvent(YAxisListener.MARKER);
    }

    /** Set the 'selected' state of this axis.
     *  <p>
     *  If this actually changes the selection, the axis will notify the listener
     *  so that it can redraw the Y axes.
     *  @param selected The new state.
     */
    public final void setSelected(boolean selected)
    {
        if (this.selected == selected)
            return;
        this.selected = selected;
        fireEvent(YAxisListener.SELECTION);
    }

    /** @return Returns <code>true</code> if this axis is selected. */
    public final boolean isSelected()
    {
        return selected;
    }

    @Override
    public void setValueRange(double low, double high)
    {
        super.setValueRange(low, high);
        fireEvent(YAxisListener.RANGE);
    }
    
    /** Auto-Zoom the value range of this Y axis to include all traces. */
    @SuppressWarnings("nls")
    public void autozoom(XAxis xaxis)
    {
        AxisRangeLimiter limiter = new AxisRangeLimiter(xaxis);
        double low = Double.MAX_VALUE;
        double high = -Double.MAX_VALUE;
        for (Trace trace : traces)
        {
            ChartSampleSequence samples = trace.getSampleSequence();
            // Lock samples so they don't change on us.
            synchronized (samples)
            {   // Any data at all?
                if (samples.size() < 1)
                    continue;
                // Autozoom only the visible part, i.e. xaxis low .. high
                int i0 = limiter.getLowIndex(samples);
                int i1 = limiter.getHighIndex(samples);
                for (int i=i0; i<=i1; ++i)
                {
                    double y = samples.get(i).getY();
                    // Ignore infinite numbers
                    if (Double.isInfinite(y)  || Double.isNaN(y))
                        continue;
                    // Otherwise update low, high.
                    if (y < low)
                        low = y;
                    if (y > high)
                        high = y;
                }
            }
        }
        
        if (low == high) {
        	//If low equals high we'll move the trace to the middle.
            high += 1.0;
            low -= 1.0;
        }
        if (low < high)
        {
            if (isLogarithmic())
            {   // Perform adjust ment in log space.
                // But first, refuse to deal with <= 0
                if (low <= 0.0)
                    low = 1;
                if (high <= low)
                    high = 100;
                low = Log10.log10(low);
                high = Log10.log10(high);
            }
            // Add a little room above & below the exact value range
            double extra = (high - low) * 0.01;
            low = low - extra;
            high = high + extra;
            if (isLogarithmic())
            {
                low = Log10.pow10(low);
                high = Log10.pow10(high);
            }
            if (Chart.debug)
                System.out.println("Autozoom " + getLabel() + " to "
                                   + low + " ... " + high);
            setValueRange(low, high);
        }
        // else: leave as is
    }

    /** The axis (label) color.
     *  <p>
     *  If there is only a single trace on this axis, that trace's color
     *  is used.
     *  Otherwise, a default color is used.
     *
     *  @return Returns the color or null.
     */
    public final Color getColor()
    {
        if (traces.size() == 1)
            return traces.get(0).getColor();
        return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
    }
    
    /** Seach all traces on this axis, return the sample that's closest
     *  to the given coordinates.
     */
    public TraceSample getClosestSample(Axis xaxis, double xval, double yval)
    {
        // N determines the pixel range that we'll search
        final int N = 10;
        ChartSample best_sample = null;
        Trace best_trace = null;
        long closest = Long.MAX_VALUE;
        // Convert the position/value into screen coordinates,
        // since we have to seach in screen space.
        int x0 = xaxis.getScreenCoord(xval);
        int y0 = this.getScreenCoord(yval);
        for (Trace trace : traces)
        {
            ChartSampleSequence samples = trace.getSampleSequence();
            synchronized (samples)
            {
                // Check vicinity of the given x (position, time stamp)
                int i0 = ChartSampleSearch.findClosestSample(samples, xval);
                // Go 'left' for N pixels
                for (int i=i0; i>=0; --i)
                {
                    ChartSample sample = samples.get(i);
                    int x = xaxis.getScreenCoord(sample.getX());
                    if (x < x0 - N)
                        break;
                    int y = getScreenCoord(sample.getY());
                    int dx = x0-x;
                    int dy = y0-y;
                    long dist = dx*dx + dy*dy;
                    if (dist < closest)
                    {
                        closest = dist;
                        best_trace = trace;
                        best_sample = sample;
                    }
                }
                // Go 'right' for N pixels
                for (int i=i0+1; i<samples.size(); ++i)
                {
                    ChartSample sample = samples.get(i);
                    int x = xaxis.getScreenCoord(sample.getX());
                    if (x > x0 + N)
                        break;
                    int y = getScreenCoord(sample.getY());
                    int dx = x0-x;
                    int dy = y0-y;
                    long dist = dx*dx + dy*dy;
                    if (dist < closest)
                    {
                        closest = dist;
                        best_trace = trace;
                        best_sample = sample;
                    }
                }
            }
        }
        if (best_trace == null)
            return null;
        return new TraceSample(best_trace, best_sample);
    }
    
    public int getPixelWidth(GC gc)
    {
        Point char_size = gc.textExtent("X"); //$NON-NLS-1$
        // Room for label (vertical) + value text + tick markers.
        return 2*char_size.y + TICK_LENGTH;
    }
    
    public Trace getTraceAt(int x, int y) 
    {
    	 for (Trace trace : traces)
         {
    		 if(trace.getLabelLayout() != null && trace.getLabelLayout().contains(x, y))
    			 return trace;
         }
    	 return null;
    }
    
    /** Paint the axis.
     *  <p>
     *  Does not paint any series data, only the axis (labels, ticks, ...)
     *  @param e Clipping information from the paint event is used for optimization)
     */
    @SuppressWarnings("nls")
    public void paint(PaintEvent e)
    {
        if (!region.intersects(e.x, e.y, e.width, e.height))
            return;
        if (Chart.debug)
            System.out.println("paint axis '" + label + "', "
                          + region.height + " pixel heigh");
        GC gc = e.gc;
        Point char_size = gc.textExtent("X"); //$NON-NLS-1$
        
        Color pbgColor = gc.getBackground();
        
        // Axis and Tick marks
        if (selected) {
        	
        	// Let's fill the background to make selected axis more clear to distinguish.
        	Color bgColor = gc.getDevice().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
        	gc.setBackground(bgColor);
        	gc.fillRectangle(region.x + 1, region.y, region.width - 2, region.height);
        	bgColor.dispose();
        	
        	// "thicken" line by drawing a parallel one
            gc.drawRectangle(
                region.x + region.width-2,
                region.y, 
                1,
                region.height-1);
        }
        else
            gc.drawLine(
                region.x + region.width-1,
                region.y, 
                region.x + region.width-1,
                region.y + region.height-1);
        computeTicks(gc);
        double last_tick = Double.NaN;
        for (double tick = ticks.getStart();
            tick <= high_value;
            tick = ticks.getNext(tick))
        {
            // Major tick marks
            int y = getScreenCoord(tick);
            gc.drawLine(region.x + region.width - TICK_LENGTH, y, 
                        region.x + region.width - 1, y);
            // Tick Label
            String mark = ticks.format(tick);
            Point mark_size = gc.textExtent(mark);
            GraphicsUtils.drawVerticalText(mark,
                    region.x + region.width - TICK_LENGTH - char_size.y,
                    y  - mark_size.x/2,
                    gc, SWT.UP);
            // Minor ticks
            if (show_minor_ticks)
            {
                if (last_tick != Double.NaN)
                {
                    final int MINOR_TICKS = 5;
                    double dist = (tick - last_tick) / MINOR_TICKS;
                    for (int i=1; i<MINOR_TICKS; ++i)
                    {
                        double minor = last_tick + dist * i;
                        int my = getScreenCoord(minor);
                        gc.drawLine(region.x + region.width - (2*TICK_LENGTH)/3, my, 
                                    region.x + region.width - 1, my);
                    }
                }
                last_tick = tick;
            }
        }
        
        paintLabel(gc);
        if (Chart.debug)
            gc.drawRectangle(region.x, region.y, region.width-1, region.height-1);
        
        gc.setBackground(pbgColor);
    }

    protected void paintLabel(GC gc)
    {
        // Label: At left edge of region, vertically apx. centered
        Color fg = gc.getForeground();
        gc.setForeground(getColor());
        Point label_size = gc.textExtent(label);
    
        GraphicsUtils.drawVerticalText(label,
                region.x + 1,
                region.y + (region.height-label_size.x)/2,
                gc, SWT.UP);
    
        gc.setForeground(fg);
    }
    
    /** Draw all the markers of this axis */
    public void paintMarkers(GC gc, Axis xaxis)
    {
        for (Marker marker : markers)
            marker.paint(gc, xaxis, this);
    }
}
