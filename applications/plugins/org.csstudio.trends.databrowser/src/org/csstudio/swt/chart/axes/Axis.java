package org.csstudio.swt.chart.axes;

import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ITicks;
import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/** Base class for X and Y axes.
 *  <p>
 *  Handles the basic screen-to-value transformation.
 *  <p>
 *  @author Kay Kasemir
 *
 */
public class Axis
{
    protected static final int TICK_LENGTH = 10;

    /** Is this a horizontal axis? Otherwise: Vertical. */
    private boolean horizontal;
    
    /** The screen region of this axis. */
    protected Rectangle region = new Rectangle(0, 0, 1, 1);
    
    /** Transformation from value into screen coordinates. */
    protected ITransform transform;
    
    /** Low end of value range. */
    protected double low_value;
    
    /** High end of value range. */
    protected double high_value;
    
    /** Helper for computing the tick marks. */
    protected Ticks ticks;
    
    /** Do we need to re-compute the ticks? */
    private boolean dirty_ticks;
    
    /** Low end of screen range. */
    private int low_screen;
    
    /** High end of screen range. */
    private int high_screen;
    
    /** Axis label. */
    protected String label;
    
    /** Constructor. */
    Axis(boolean horizontal, String label, Ticks ticks)
    {
        this(horizontal, label, ticks, new LinearTransform());
    }
    
    /** Constructor. */
    Axis(boolean horizontal, String label, Ticks ticks, ITransform transform)
    {
        this.horizontal = horizontal;
        this.transform = transform;
        this.ticks = ticks;
        dirty_ticks = true;
        low_screen = 0;
        high_screen = 10;
        low_value = 0;
        high_value = 10;
        this.label = label;
    }
    
    /** @return Returns the label. */
    public final String getLabel()
    {
        return label;
    }

    /** Get the screen coordinates of the given value.
     *  <p>
     *  Values are mapped from value to screen coordinates via
     *  'transform', except for infinite values, which get mapped
     *  to the edge of the screen range.
     *  
     *  @return Returns the value transformed in screen coordinates.
     */
    public final int getScreenCoord(double value)
    {
        if (Double.isInfinite(value))
            return low_screen;
        return (int)(transform.transform(value)+0.5);
    }
    
    /** @return Returns screen coordinate transformed into a value. */
    final public double getValue(int coord)
    {
        return transform.inverse(coord);
    }

    /** @return Returns low end of axis value range. */
    final public double getLowValue()
    {
        return low_value;
    }
    
    /** @return Returns high end of axis value range. */
    final public double getHighValue()
    {
        return high_value;
    }

    /** Set the new value range.
     *  @return <code>true</code> if this actually did something. */
    @SuppressWarnings("nls")
    public boolean setValueRange(double low, double high)
    {
        if (low >= high)
        {
            Plugin.logError("Axis " + getLabel() 
                      + ": Cannot set value range to " + low + " ... " + high);
            return false;
        }
        dirty_ticks = true;
        low_value = low;
        high_value = high;
        transform.config(low_value, high_value, low_screen, high_screen);
        return true;
    }

    /** Tell the axis where it is on the screen.
     *  @param region The on-screen region to set.
     *  @see #setScreenRange(int, int)
     */
    public void setRegion(int x, int y, int width, int height)
    {
        region.x = x;
        region.y = y;
        region.width = width;
        region.height = height;

        if (horizontal)
            setScreenRange(x, x + width-1);
        else
            setScreenRange(y + height-1, y);
    }

    /** Update the screen coordinate range of the axis. */
    protected void setScreenRange(int low, int high)
    {
        dirty_ticks = true;
        low_screen = low;
        high_screen = high;
        transform.config(low_value, high_value, low_screen, high_screen);
    }
        
    /** @return Returns the region used by this axis on the screen. */
    final public Rectangle getRegion()
    {
        return region;
    }
    
    /** @return Tick mark information. */
    public ITicks getTicks()
    {
        return ticks;
    }
    
    @SuppressWarnings("nls")
    protected void computeTicks(GC gc)
    {
        if (dirty_ticks)
        {
            if (Chart.debug)
                System.out.println("Compute ticks for '" + label + "'");
            if (horizontal)
                ticks.compute(low_value, high_value, gc, region.width);
            else
                ticks.compute(low_value, high_value, gc, region.height);
            dirty_ticks = false;
        }
    }
}
