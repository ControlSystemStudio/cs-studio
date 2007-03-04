package org.csstudio.swt.chart;

import org.csstudio.swt.chart.axes.YAxis;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;

/** A trace is sequence of samples with color, assiged Y Axis, ...
 *  <p>
 *  After providing the samples via a <code>SampleSequence</code>
 *  interface, one needs to wrap them as a <code>Trace</code> and add that
 *  to a chart.
 *  
 *  @see ChartSampleSequence
 * 
 *  @author Kay Kasemir
 */
public class Trace
{
    private String name;
    private ChartSampleSequence samples;
    private Color  color;
    private int line_width;
    private YAxis  yaxis;
    private Rectangle labelLayout;
    private Type type;
    private boolean isAutoScalable;
    private double defaultScaleMax;
    private double defaultScaleMin;
    
    public enum Type 
    {
    	Lines,
    	Markers,
    	Candlestick,
    	MinMaxAverage,
    	Bars
    }
    
    public Trace(String name, ChartSampleSequence series, Color color,
            int line_width, YAxis yaxis) {
    	
    	this(name, series, color, line_width, yaxis, 0, 10, Type.Lines, true);
    }
    
    /** Create a new trace.
     * 
     *  @param series The SampleSeries interface.
     *  @param color The color to use.
     *               <b>Note:</b> The trace does not assume ownerwhip of the color!
     *  @param yaxis The axis to which the trace attaches.
     */
    public Trace(String name, ChartSampleSequence series, Color color,
                    int line_width, YAxis yaxis, double defaultScaleMin, double defaultScaleMax,
                    Trace.Type type, boolean autoScale)
    {
        this.name = name;
        this.samples = series;
        this.color = color;
        this.line_width = line_width;
        this.yaxis = yaxis;
        this.type = type;
        this.isAutoScalable = autoScale;
        this.defaultScaleMax = defaultScaleMax;
        this.defaultScaleMin = defaultScaleMin;
        yaxis.addTrace(this);
    }

    /** Dispose the trace.
     *  <p>
     *  Detaches trace from axis, disposes the color.
     */
    public void dispose()
    {
        yaxis.removeTrace(this);
        color = null;
    }

    /** @return Returns the name of this trace. */
    public final String getName()
    {
    	return name;
    }
    
    /** @return Returns the name of this trace as it is displayed on the chart. */
    public final String getDisplayName() 
    {
    	if(!this.getIsAutoScalable())
    		return this.getName();
    	
    	return this.getName() + "*"; //$NON-NLS-1$
    }

    /** @return Returns the SampleSequence interface for this trace. */
    public final ChartSampleSequence getSampleSequence()
    {
        return samples;
    }

    /** @return Returns the trace color. */
    public final Color getColor()
    {
        return color;
    }
    
    /** Set the trace to a new color. */
    public final void setColor(Color new_color)
    {
        color = new_color;
    }
    
    /** @return Returns the trace line width. */
    public final int getLineWidth()
    {
        return line_width;
    }
    
    /** Set the trace to a new line width. */
    public final void setLineWidth(int new_width)
    {   // >=0 is important, upper limit is somewhat arbitrary
        if (new_width >= 0  &&  new_width < 100)
            line_width = new_width;
        else
            line_width = 0;
    }

    /** @return Returns the y-axis. */
    public final YAxis getYAxis()
    {
        return yaxis;
    }

    /** @param yaxis The y-axis to set. */
    void setYAxis(YAxis yaxis)
    {
        this.yaxis = yaxis;
    }
    
    public void setIsAutoScalable(boolean scalable) 
    {
    	isAutoScalable = scalable;
    }
    
    public final boolean getIsAutoScalable()
    {
    	return isAutoScalable;
    }
    
    /** @return Returns trace type */
    public final Trace.Type getType() 
    {
    	return type;
    }
    
    public final Rectangle getLabelLayout() 
    {
    	return labelLayout;
    }
    
    public final void setLabelLayout(Rectangle layout) 
    {
    	this.labelLayout = layout;
    }
    
    public double getDefaultScaleMax() 
    {
    	return this.defaultScaleMax;
    }
    
    public double getDefaultScaleMin() 
    {
    	return this.defaultScaleMin;
    }
}
