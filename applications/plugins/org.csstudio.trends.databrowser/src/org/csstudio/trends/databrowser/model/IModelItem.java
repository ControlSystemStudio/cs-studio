package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.csstudio.swt.chart.TraceType;
import org.eclipse.swt.graphics.Color;

/** Public interface to one item of the Model.
 *  <p>
 *  Provides name, color, samples etc. for one item in the model.
 *  In addition, it functions as an IProcessVariable so that other
 *  CSS tools can get the PV, and an IProcessVariableWithSamples
 *  to provide access to the <u>visible</u> samples of the item.
 *  @see Model
 *  @author Kay Kasemir
 */
public interface IModelItem extends IProcessVariableWithSamples
{
    /**  @param index of <u>visible</u> Sample 
     *   @return the Sample with index
     *   @see IProcessVariableWithSamples#size()
     */
    public ModelSample getModelSample(final int index);

    /** @return The engineering units string. */
    public String getUnits();
    
    /** Change the name of this entry. */
    public void changeName(String new_name);
    
	/** @return The axis index. */
	public int getAxisIndex();
    
    /** Set a new axis index. */
    public void setAxisIndex(int axis);
    
    /** @return The lower Y-axis limit. */
    public double getAxisLow();

    /** @return The upper Y-axis limit. */
    public double getAxisHigh();

    /** Set lower Y-axis limit. */
    public void setAxisLow(double limit);
    
    /** Set upper Y-axis limit. */
    public void setAxisHigh(double limit);
    
    /** @return <code>true</code> if item is visible. */
    public boolean isVisible();
    
    /** Make item visible or hide it.
     *  <p>
     *  Making an item invisible might be useful to temporarily
     *  clear up the display, or for items that are used as
     *  formula inputs yet aren't to be shown themselves.
     *  @param yesno <code>true</code> means visible.
     */
    public void setVisible(boolean yesno);

    /** Show the axis?
     *  Trace will still show, this only controls
     *  visibility of the axis
     *  @see #setVisible(boolean)
     *  @see #setAxisVisible(boolean)
     */
    public boolean isAxisVisible();

    /** Make axis visible or hide it
     *  @param visible <code>true</code> to display axis
     *  @see #isAxisVisible()
     */
    public void setAxisVisible(boolean visible);

    /** @return Returns <code>true</code> if trace should be auto-scaled. */
    public boolean getAutoScale();
    
    /** Set auto-scale mode. */
    public void setAutoScale(boolean auto_scale); 
    
    /** Get the color of this item.
     *  Note that the item own the color.
     *  Do NOT dispose this color!
     *  @return The color.
     */
	public Color getColor();

	/** Set item to a new color. */
	public void setColor(Color new_color);
	
	/** @return Returns the trace line width. */
    public int getLineWidth();
    
    /** Set the trace to a new line width. */
    public void setLineWidth(int new_width);
    
    /** @return Returns current trace type */
    public TraceType getTraceType();
    
    /** Set new trace type for this model. */
    public void setTraceType(TraceType new_trace_type);
    
    /** @return <code>true</code> if using log. scale */
    public boolean getLogScale();

    /** Configure to use log. scale or not. */
    public void setLogScale(boolean use_log_scale);

	/** Get the samples of this model item.
     *  <p>
     *  <b>Note:</b> The returned sample interface is passed to
     *  the chart. So when the data of this model item changes
     *  (new samples added, ...), the actual reference to this item's
     *  sample interface should stay the same, just the sample count
     *  and sample instances provided by that interface will differ!
     *  @return The samples.
     */
	public IModelSamples getSamples();
}