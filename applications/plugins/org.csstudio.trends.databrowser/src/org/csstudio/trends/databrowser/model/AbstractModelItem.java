package org.csstudio.trends.databrowser.model;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLHelper;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.csstudio.swt.chart.ChartSampleSearch;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.swt.graphics.Color;
import org.w3c.dom.Element;

/** Base class for IModelItem implementations
 *  @author Kay Kasemir
 */
public abstract class AbstractModelItem
    extends PlatformObject
    implements IModelItem
{
    // Tag names used to write/read XML
    static final String TAG_PV = "pv"; //$NON-NLS-1$
    static final String TAG_FORMULA = "formula"; //$NON-NLS-1$
    static final String TAG_ARCHIVE = "archive"; //$NON-NLS-1$
    static final String TAG_KEY = "key"; //$NON-NLS-1$
    static final String TAG_URL = "url"; //$NON-NLS-1$
    static final String TAG_COLOR = "color"; //$NON-NLS-1$
    static final String TAG_TRACE_TYPE = "trace_type"; //$NON-NLS-1$
    static final String TAG_LOG_SCALE = "log_scale"; //$NON-NLS-1$
    static final String TAG_BLUE = "blue"; //$NON-NLS-1$
    static final String TAG_GREEN = "green"; //$NON-NLS-1$
    static final String TAG_RED = "red"; //$NON-NLS-1$
    static final String TAG_AUTOSCALE = "autoscale"; //$NON-NLS-1$
    static final String TAG_VISIBLE = "visible"; //$NON-NLS-1$
    static final String TAG_MAX = "max"; //$NON-NLS-1$
    static final String TAG_MIN = "min"; //$NON-NLS-1$
    static final String TAG_LINEWIDTH = "linewidth"; //$NON-NLS-1$
    static final String TAG_AXIS = "axis"; //$NON-NLS-1$
    static final String TAG_NAME = "name"; //$NON-NLS-1$
    static final String TAG_INPUT = "input"; //$NON-NLS-1$
    
    /** The model to which this item belongs. */
    final protected Model model;
    
    /** The name of this item. */
    protected String name;
    
    /** Start/end index of visible sample range */
    private int start_index, end_index;
    
    /** The units of this item. */
    protected String units = ""; //$NON-NLS-1$
    
    /** The Y axis to use. */
    protected int axis_index;
    
    /** Y-axis range. */
    protected double axis_low, axis_high;
    
    /** Is the item visible? */
    protected boolean visible;
    
    /** Is the axis visible? */
    private boolean axis_visible;
    
    /** Auto scale trace */
    protected boolean auto_scale;

    /** The color for this item.
     *  <p>
     *  Issue:
     *  Using the SWT Color binds the 'model' to the GUI library.
     *  But only keeping red/green/blue data in the 'model'
     *  results in a lot of code for allocating and freeing the Color
     *  all over the place, so after some back and force I decided
     *  to have the color in the model.
     */
    protected Color color;
    
    /** The line width for this item. */
    protected int line_width;
    
    /** The trace type (line, ...) */
    protected TraceType trace_type;
    
    /** Use log scale? */
    protected boolean log_scale;
    
    AbstractModelItem(final Model model, final String pv_name,
            final int axis_index, final double min, final double max,
            final boolean visible,
            final boolean auto_scale,
            final int red, final int green, final int blue,
            final int line_width,
            final TraceType trace_type,
            final boolean log_scale)
    {
        this.model = model;
        name = pv_name;
        this.axis_index = axis_index;
        this.axis_low = min;
        this.axis_high = max;
        this.visible = visible;
        // TODO get from config
        axis_visible = true;
        this.auto_scale = auto_scale;
        this.color = new Color(null, red, green, blue);
        this.line_width = line_width;
        this.trace_type = trace_type;
        this.log_scale = log_scale;
    }
    
    /** @return Model to which this item belongs. */
    public Model getModel()
    {   return model;    }
    
    /** Must be called to dispose the color. */
    public void dispose()
    {
        color.dispose();
    }
    
    /** @see IProcessVariable */
    final public String getTypeId()
    {   return IProcessVariableWithSamples.TYPE_ID;   }

    /** @return Name of PV or Formula
     *  @see IProcessVariable
     */
    final public String getName()
    {   return name;  }
    
    /** @return the Number of <u>visible</u> Samples
     *  @see #getSample(int)
     *  @see IProcessVariableWithSamples
     */
    @SuppressWarnings("nls")
    final public int size()
    {
        // Determine visible start/end range
        final ITimestamp start = model.getStartTime();
        final ITimestamp end = model.getEndTime();
        final IModelSamples all_samples = getSamples();
        synchronized (all_samples)
        {
            start_index =
                ChartSampleSearch.findClosestSample(all_samples, start.toDouble());
            end_index =
                ChartSampleSearch.findClosestSample(all_samples, end.toDouble());
        }
        if (start_index < end_index)
        {
            Plugin.getLogger().debug(getName() + ".size(): " +
                    start_index + " ... " + end_index);
            return end_index - start_index + 1;
        }
        Plugin.getLogger().debug(getName() + ".size(): 0");
        return 0;
    }
    
    /** @param index of <u>visible</u> Sample 
    *   @return the Sample with index
    *   @see #size()
    *   @see IProcessVariableWithSamples
    */
    final public IValue getSample(final int index)
    {
        return getModelSample(index).getSample();
    }

    /**  @param index of <u>visible</u> Sample 
     *   @return the Sample with index
     *   @see #size()
     *   @see IProcessVariableWithSamples
     */
     final public ModelSample getModelSample(final int index)
     {
         final IModelSamples all_samples = getSamples();
         synchronized (all_samples)
         {
             return all_samples.get(start_index + index);
         }
     }
    
    /** Base implementation for changing the name.
     *  Derived classes might also need to change PV names etc.
     *  @see IModelItem#changeName(String)
     */
    public void changeName(final String new_name)
    {
        // Avoid duplicates, do not allow if new name already in model.
        if (model.findItem(new_name) != null)
            return;
        // Name change looks like remove/add back in
        model.fireEntryRemoved(this);
        // Now change name
        name = new_name;
        // and add
        model.fireEntryAdded(this);
    }
    
    /** @see IModelItem#getUnits() */
    final public String getUnits()
    {   return units;  }

    /** @see IModelItem#getAxisIndex() */
    final public int getAxisIndex()
    {   return axis_index;  }
    
    /** @see IModelItem#setAxisIndex(int) */
    final public void setAxisIndex(final int axis)
    {
        if (axis_index == axis)
            return;
        axis_index = axis;
        // Use axis limits and type of other model items on this axis
        for (int i=0; i<model.getNumItems(); ++i)
        {
            final IModelItem item = model.getItem(i);
            if (item != this  &&  item.getAxisIndex() == axis_index)
            {
                setAxisLimitsSilently(item.getAxisLow(), item.getAxisHigh());
                setLogScaleSilently(item.getLogScale());
                break;
            }
        }
        model.fireEntryConfigChanged(this);
    }
    
    /** @see IModelItem#getAxisLow() */
    final public double getAxisLow()
    {   return axis_low; }

    /** @see IModelItem#getAxisHigh() */
    final public double getAxisHigh()
    {   return axis_high; }

    /** @see IModelItem#setAxisLow(double) */
    final public void setAxisLow(final double limit)
    {   
        axis_low = limit;
        model.setAxisLimits(axis_index, axis_low, axis_high);
    }

    /** @see IModelItem#setAxisHigh(double) */
    final public void setAxisHigh(final double limit)
    {   
        axis_high = limit;
        model.setAxisLimits(axis_index, axis_low, axis_high);
    }

    /** Set axis limits, but don't inform model.
     *  <p>
     *  Used by model to avoid recursion that would result from setAxisMin/Max.
     */
    final public void setAxisLimitsSilently(final double low, final double high)
    {
        axis_low = low;
        axis_high = high;
    }

    /** @see IModelItem#isVisible() */
    final public boolean isVisible()
    {
        return visible;
    }
    
    /** @see IModelItem#setVisible(boolean) */
    final public void setVisible(final boolean yesno)
    {
        if (visible == yesno)
            return; // no change
        visible = yesno;
        // Notify model of this change.
        model.fireEntryConfigChanged(this);
    }
    
    final public boolean isAxisVisible()
    {
       return axis_visible; 
    }

    /** Make axis visible or hide it
     *  @param axis_visible <code>true</code> to display axis
     *  @see #isAxisVisible()
     */
    final public void setAxisVisible(final boolean axis_visible)
    {
        // Notify model of this change because it affects all
        // items on same axis
        model.setAxisVisible(axis_index, axis_visible);
    }

    /** Make axis visible or hide it
     *  @param axis_visible <code>true</code> to display axis
     *  <p>
     *  For internal use by the model to avoid recursion
     *  as would happen with setAxisVisible.
     *  @see #setAxisVisible()
     */
    final public void setAxisVisibleSilently(final boolean axis_visible)
    {
        this.axis_visible = axis_visible;
    }
    
    /** @see IModelItem#setAutoScale(boolean) */
    final public void setAutoScale(final boolean auto_scale) 
    {
        // Notify model of this change because it affects all
        // items on same axis
        model.setAutoScale(axis_index, auto_scale);
    }
    
    /** Configure to use auto scale or not.
     *  <p>
     *  For internal use by the model to avoid recursion
     *  as would happen with setAutoScale.
     *  @see #setLogScale(boolean)
     */
    final void setAutoScaleSilently(final boolean auto_scale) 
    {
        this.auto_scale = auto_scale;
    }
    
    /** @see IModelItem#getAutoScale() */
    final public boolean getAutoScale()
    {   return auto_scale; }
    
    /** @see IModelItem#getColor() */
    final public Color getColor()
    {   return color; }
    
    /** @see IModelItem#setColor(Color) */
    final public void setColor(final Color new_color)
    {
        color.dispose();
        color = new_color;
        // Notify model of this change.
        model.fireEntryConfigChanged(this);
    }
    
    /** @return Returns the trace line width. */
    final public int getLineWidth()
    {
        return line_width;
    }
    
    /** Set the trace to a new line width. */
    final public void setLineWidth(final int new_width)
    {
        line_width = new_width;
        // Notify model of this change.
        model.fireEntryConfigChanged(this);
    }
    
    /** @return Returns current model display type */
    final public TraceType getTraceType() 
    {
        return trace_type;
    } 
    
    /** Set new display type */
    final public void setTraceType(final TraceType new_trace_type) 
    {
        if(trace_type == new_trace_type)
            return;
        trace_type = new_trace_type;
        // Notify model of this change.
        model.fireEntryConfigChanged(this);
    }
    
    /** @return <code>true</code> if using log. scale */
    final public boolean getLogScale()
    {
        return log_scale;
    }

    /** Configure to use log. scale or not. */
    final public void setLogScale(final boolean use_log_scale)
    {
        // Notify model of this change.
        model.setLogScale(axis_index, use_log_scale);
    }

    /** Configure to use log. scale or not.
     *  <p>
     *  For internal use by the model to avoid recursion
     *  as would happen with setLogScale.
     *  @see #setLogScale(boolean)
     */
    final void setLogScaleSilently(final boolean use_log_scale)
    {
        log_scale = use_log_scale;
    }
    
    /** @return Returns an XML string for this item. */
    abstract public String getXMLContent();
    
    /** Add the XML for the common config elements to the string buffer.
     *  @see #getXMLContent()
     */
    @SuppressWarnings("nls")
    protected void addCommonXMLConfig(final StringBuilder b)
    {
        XMLHelper.XML(b, 3, TAG_NAME, name);
        XMLHelper.XML(b, 3, TAG_AXIS, Integer.toString(axis_index));
        XMLHelper.XML(b, 3, TAG_LINEWIDTH, Integer.toString(line_width));
        XMLHelper.XML(b, 3, TAG_MIN, Double.toString(axis_low));
        XMLHelper.XML(b, 3, TAG_MAX, Double.toString(axis_high));
        XMLHelper.XML(b, 3, TAG_VISIBLE, Boolean.toString(isVisible()));
        XMLHelper.XML(b, 3, TAG_AUTOSCALE, Boolean.toString(getAutoScale()));
        XMLHelper.indent(b, 3); b.append("<" + TAG_COLOR + ">\n");
        XMLHelper.XML(b, 4, TAG_RED, Integer.toString(color.getRed()));
        XMLHelper.XML(b, 4, TAG_GREEN, Integer.toString(color.getGreen()));
        XMLHelper.XML(b, 4, TAG_BLUE, Integer.toString(color.getBlue()));
        XMLHelper.indent(b, 3); b.append("</" + TAG_COLOR + ">\n");
        XMLHelper.XML(b, 3, TAG_LOG_SCALE, Boolean.toString(getLogScale()));
        XMLHelper.XML(b, 3, TAG_TRACE_TYPE, getTraceType().name());
    }
    
    /** Helper for loading RGB colors from DOM. */
    public static int [] loadColorFromDOM(final Element pv)
    {
    	return loadColorFromDOM(pv, TAG_COLOR, new int[] { 0, 0, 255 });
    }

    /** Helper for loading RGB colors from DOM.
     *  @param pv Parent element in XML DOM
     *  @param tag Tag name that holds colors
     *  @param default_rgb Default to use when nothing found
     *  @return RGB values 0..255
     */
    public static int [] loadColorFromDOM(final Element pv,
    		final String tag, final int default_rgb[])
    {
        final int rgb[] = new int[3];
        final Element color =
            DOMHelper.findFirstElementNode(pv.getFirstChild(), tag);
        if (color != null)
        {
            rgb[0] = DOMHelper.getSubelementInt(color, TAG_RED, 0);
            rgb[1] = DOMHelper.getSubelementInt(color, TAG_GREEN, 0);
            rgb[2] = DOMHelper.getSubelementInt(color, TAG_BLUE, 255);
            return rgb;
        }
        return default_rgb;
    }
    
    /** Helper for loading trace type from DOM. */
    protected static TraceType loadTraceTypeFromDOM(final Element pv)
    {
        String trace_type_txt = DOMHelper.getSubelementString(pv, TAG_TRACE_TYPE);
        if (trace_type_txt.length() > 0)
            return TraceType.fromName(trace_type_txt);
        return TraceType.Lines;
    }
}
