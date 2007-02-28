package org.csstudio.trends.databrowser.model;

import java.util.ArrayList;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.cache.ArchiveCache;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.util.xml.DOMHelper;
import org.csstudio.util.xml.XMLHelper;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.csstudio.utility.pv.epics.EPICS_V3_PV;
import org.csstudio.value.DoubleValue;
import org.csstudio.value.EnumValue;
import org.csstudio.value.IntegerValue;
import org.csstudio.value.MetaData;
import org.csstudio.value.NumericMetaData;
import org.csstudio.value.StringValue;
import org.csstudio.value.Value;
import org.eclipse.core.runtime.PlatformObject;
import org.csstudio.archive.cache.CachingArchiveServer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Element;

/** One item (channel, PV, line, ...) of the <code>ChartModel</code>
 *  @see Model
 *  @author Kay Kasemir
 */
public class ModelItem 
       extends PlatformObject
       implements IModelItem, PVListener, IProcessVariable
{	
	/** The model to which this item belongs. */
	private Model model;
	
    /** The name of this Chart Item. */
    private String name;
    
    /** The units of this Chart Item. */
    private String units = ""; //$NON-NLS-1$
    
    /** The Y axis to use. */
    private int axis_index;
    
    /** The type of data being passed to viewer */
    private int data_type;
    
    /** The count of bins */
    private int bins;
    
    /** The display type */
    private IModelItem.DisplayType display_type;
    
    /** Y-axis range. */
    private double axis_low, axis_high;
    
    /** Auto scale trace */
    private boolean isTraceAutoScalable;
    
    /** The color for this item.
     *  <p>
     *  Issue:
     *  Using the SWT Color binds the 'model' to the GUI library.
     *  But only keeping red/green/blue data in the 'model'
     *  results in a lot of code for allocating and freeing the Color
     *  all over the place, so after some back and force I decided
     *  to have the color in the model.
     */
    private Color color;
    
    /** The line width for this item. */
    private int line_width;
    
    /** Use log scale? */
    private boolean log_scale;
    
    /** The control system PV from which to get new values. */
    private PV pv;
    
    /** The most recent value we got from the pv.
     *  <p>
     *  Read from the GUI thread, updated from a PV monitor thread.
     */
    private volatile Value current_value;
    
    /** Where to get archived data for this item. */
    private ArrayList<IArchiveDataSource> archives
        = new ArrayList<IArchiveDataSource>();
    
    /** All the samples of this model item. */
    private ModelSamples samples;
        
    /** Constructor
     *  @param pv_name Name of the PV
     *  @param axis_index The Y axis to use [0, 1, ...]
     *  @param min,
     *  @param max The Y axis range
     *  @param red,
     *  @param green,
     *  @param blue The color to use.
     *  <p>
     *  There is no <code>dispose()</code>,
     *  but the ChartItem must not be running when released.
     *  @see #stop
     */
    public ModelItem(Model model, String pv_name, int ring_size,
    		int axis_index, double min, double max,
            int red, int green, int blue,
            int line_width, boolean log_scale)
    {
    	this.model = model;
        name = pv_name;
        this.axis_index = axis_index;
        this.axis_low = min;
        this.axis_high = max;
        this.color = new Color(null, red, green, blue);
        this.line_width = line_width;
        this.log_scale = log_scale;
        this.data_type = 1;
        this.bins = 400;
        this.display_type = DisplayType.Lines;
        this.isTraceAutoScalable = true;
        pv = new EPICS_V3_PV(pv_name);
        samples = new ModelSamples(ring_size);
    }    
    
    /** Must be called to dispose the color. */
    public void dispose()
    {
        if (pv.isRunning())
            pv.stop();
        color.dispose();
        archives.clear();
        archives = null;
        samples.dispose();
    }
    
    /** @see IModelItem#getName() */
    public final String getName()
    {   return name;  }
    
    
    // @see IProcessVariable
    public String getTypeId()
    {   return IProcessVariable.TYPE_ID;   }

    /** @see IModelItem#getUnits() */
    public final String getUnits()
    {   return units;  }

    /** @see IModelItem#changeName(String) */
    public void changeName(String new_name)
    {
        // Avoid duplicates, do not allow if new name already in model.
        if (model.findEntry(new_name) >= 0)
            return;
        boolean was_running = pv.isRunning();
        // TODO: I've seen null pointer errors in stop
        // when called from here, but have not been able to
        // reproduce them.
        if (was_running)
            stop();
        // Name change looks like remove/add back in
        model.fireEntryRemoved(this);
        // Now change name
        name = new_name;
        pv = new EPICS_V3_PV(name);
        // and add
        model.fireEntryAdded(this);
        if (was_running)
            start();
    }
    
    /** @see IModelItem#getAxisIndex() */
    public final int getAxisIndex()
    {
        return axis_index;
    }
    
    /** @see IModelItem#setAxisIndex(int) */
	public void setAxisIndex(int axis)
	{
		if (axis_index == axis)
			return;
		axis_index = axis;
        // Adapt to axis limits and type of other model items on this axis
        for (int i=0; i<model.getNumItems(); ++i)
        {
            IModelItem item = model.getItem(i);
            if (item != this  &&  item.getAxisIndex() == axis_index)
            {
                setAxisLimitsSilently(item.getAxisLow(), item.getAxisHigh());
                setLogScaleSilently(item.getLogScale());
                break;
            }
        }
		model.fireEntryConfigChanged(this);
	}

    /** @return The lower Y-axis limit. */
    public double getAxisLow()
    {   return axis_low; }

    /** @return The upper Y-axis limit. */
    public double getAxisHigh()
    {   return axis_high; }

    /** Set lower Y-axis limit. */
    public void setAxisLow(double limit)
    {   
        axis_low = limit;
        model.setAxisLimits(axis_index, axis_low, axis_high);
    }

    /** Set upper Y-axis limit. */
    public void setAxisHigh(double limit)
    {   
        axis_high = limit;
        model.setAxisLimits(axis_index, axis_low, axis_high);
    }

    /** Set axis limits, but don't inform model.
     *  <p>
     *  Used by model to avoid recursion that would result from setAxisMin/Max.
     */
    void setAxisLimitsSilently(double low, double high)
    {
        axis_low = low;
        axis_high = high;
    }
    
    /** @see IModelItem#getColor() */
    public final Color getColor()
    {
        return color;
    }
    
    /** @see IModelItem#setColor(Color) */
    public void setColor(Color new_color)
    {
    	color.dispose();
    	color = new_color;
    	// Notify model of this change.
    	model.fireEntryConfigChanged(this);
    }
    
    public final int getDataType() 
    {	
    	return data_type;
    }
    
    public void setDataType(int new_data_type) 
    {	
    	if(data_type == new_data_type)
    		return;
    	
    	data_type = new_data_type;

    	// Notify model of this change.
    	model.fireEntryConfigChanged(this);
    }
    
    public final int getBins() {
    	return bins;
    }
    
    public void setBins(int new_bins) 
    {	
    	if(bins == new_bins)
    		return;
    	
    	bins = new_bins;
    	
    	// Clear cache.
    	clearSampleCache();
    	// Notify model of this change.
        model.fireEntryConfigChanged(this);
    }
    
    public void setIsTraceAutoScalable(boolean scalable) 
    {
    	if(scalable != isTraceAutoScalable) 
    	{
    		isTraceAutoScalable = scalable;
    		// Notify model of this change.
    		model.fireEntryConfigChanged(this);
    	}
    }
    
    public final boolean getIsTraceAutoScalable()
    {
    	return isTraceAutoScalable;
    }
    
    /** @return Returns the trace line width. */
    public int getLineWidth()
    {
        return line_width;
    }
    
    /** Set the trace to a new line width. */
    public void setLineWidth(int new_width)
    {
        line_width = new_width;
        // Notify model of this change.
        model.fireEntryConfigChanged(this);
    }
    
    /** @return Returns current model display type */
    public IModelItem.DisplayType getDisplayType() 
    {
    	return this.display_type;
    } 
    
    /** Set new display type */
    public void setDisplayType(IModelItem.DisplayType new_display_type) 
    {
    	if(display_type == new_display_type)
    		return;
    	
    	clearSampleCache();
    	
    	display_type = new_display_type;
    	// Notify model of this change.
    	model.fireEntryConfigChanged(this);
    }
    
    /** @return <code>true</code> if using log. scale */
    public boolean getLogScale()
    {
        return log_scale;
    }

    /** Configure to use log. scale or not. */
    public void setLogScale(boolean use_log_scale)
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
    void setLogScaleSilently(boolean use_log_scale)
    {
        log_scale = use_log_scale;
    }
    
    /** @see IModelItem#getSamples() */
    public final ModelSamples getSamples()
    {
        return samples;
    }
    
    /** @see IModelItem#addSamples() */
    @SuppressWarnings("nls")
    public void addArchiveSamples(ArchiveValues archive_samples)
    {
        samples.add(archive_samples);
    }
    
    /** @see IModelItem#getArchiveDataSources() */
    public IArchiveDataSource[] getArchiveDataSources()
    {
        IArchiveDataSource result[] = new IArchiveDataSource[archives.size()];
        return archives.toArray(result);
    }
    
    /** Add another archive data source. */
    public void addArchiveDataSource(IArchiveDataSource archive)
    {
        // Ignore duplicates
        for (IArchiveDataSource arch : archives)
            if (arch.getKey() == archive.getKey() &&
                arch.getUrl().equals(archive.getUrl()))
                return;
        archives.add(archive);
        // Notify model of this change.
        model.fireEntryArchivesChanged(this);
    }

    /** Remove given archive data source. */
    public void removeArchiveDataSource(IArchiveDataSource archive)
    {
        // Remove all matching entries (should be at most one...)
        for (int i = 0; i < archives.size(); ++i)
        {
            IArchiveDataSource entry = archives.get(i);
            if (entry.getKey() == archive.getKey() &&
                entry.getUrl().equals(archive.getUrl()))
                archives.remove(i); // changes size(), but that's OK
        }
        // Notify model of this change.
        model.fireEntryArchivesChanged(this);
    }
    
    /** Move given archive data source 'up' in the list. */
    public void moveArchiveDataSourceUp(IArchiveDataSource archive)
    {
        // Move first matching entry, _skipping_ the top one!
        for (int i = 1/*!*/; i < archives.size(); ++i)
        {
            IArchiveDataSource entry = archives.get(i);
            if (entry.getKey() == archive.getKey() &&
                entry.getUrl().equals(archive.getUrl()))
            {
                entry = archives.remove(i);
                archives.add(i-1, entry);
                // Notify model of this change.
                model.fireEntryArchivesChanged(this);
                return;
            }
        }
    }
    
    /** Move given archive data source 'down' in the list. */
    public void moveArchiveDataSourceDown(IArchiveDataSource archive)
    {
        // Move first matching entry, _skipping_ the last entry!
        for (int i = 0; i < archives.size()-1/*!*/; ++i)
        {
            IArchiveDataSource entry = archives.get(i);
            if (entry.getKey() == archive.getKey() &&
                entry.getUrl().equals(archive.getUrl()))
            {
                entry = archives.remove(i);
                archives.add(i+1, entry);
                // Notify model of this change.
                model.fireEntryArchivesChanged(this);
                return;
            }
        }
    }

    /** @return Returns an XML string for this item. */
    @SuppressWarnings("nls")
    public final String getXMLContent()
    {
        StringBuffer b = new StringBuffer();
        b.append("        <pv>\n");
        XMLHelper.XML(b, 3, "name", name);
        XMLHelper.XML(b, 3, "axis", Integer.toString(axis_index));
        XMLHelper.XML(b, 3, "linewidth", Integer.toString(line_width));
        XMLHelper.XML(b, 3, "min", Double.toString(axis_low));
        XMLHelper.XML(b, 3, "max", Double.toString(axis_high));
        XMLHelper.indent(b, 3); b.append("<color>\n");
        XMLHelper.XML(b, 4, "red", Integer.toString(color.getRed()));
        XMLHelper.XML(b, 4, "green", Integer.toString(color.getGreen()));
        XMLHelper.XML(b, 4, "blue", Integer.toString(color.getBlue()));
        XMLHelper.indent(b, 3); b.append("</color>\n");
        XMLHelper.XML(b, 4, "log_scale", Boolean.toString(getLogScale()));
        if (archives.size() > 0)
        {
            for (IArchiveDataSource archive : archives)
            {
                XMLHelper.indent(b, 3);b.append("<archive>\n");
                XMLHelper.XML(b, 4, "name", archive.getName());
                XMLHelper.XML(b, 4, "url", archive.getUrl());
                XMLHelper.XML(b, 4, "key", Integer.toString(archive.getKey()));
                XMLHelper.indent(b, 3);b.append("</archive>\n");
            }
        }
        b.append("        </pv>\n");
        return b.toString();
    }
    
    /** Decode XML DOM element for "pv ...". */
    @SuppressWarnings("nls")
    public static ModelItem loadFromDOM(Model model, Element pv, int ring_size) throws Exception
    {
        // Common PV stuff
        String name = DOMHelper.getSubelementString(pv, "name");
        int axis_index = DOMHelper.getSubelementInt(pv, "axis", 0);
        int line_width = DOMHelper.getSubelementInt(pv, "linewidth", 0);
        double min = DOMHelper.getSubelementDouble(pv, "min", 0.0);
        double max = DOMHelper.getSubelementDouble(pv, "max", 10.0);
        int red, green, blue;
        Element color =
            DOMHelper.findFirstElementNode(pv.getFirstChild(), "color");
        if (color != null)
        {
            red = DOMHelper.getSubelementInt(color, "red", 0);
            green = DOMHelper.getSubelementInt(color, "green", 0);
            blue = DOMHelper.getSubelementInt(color, "blue", 255);
        }
        else
        {
            red = 0;
            green = 0;
            blue = 255;
        }
        boolean log_scale = DOMHelper.getSubelementBoolean(pv, "log_scale");
        ModelItem item =
            new ModelItem(model, name, ring_size, axis_index, min, max,
                          red, green, blue, line_width, log_scale);
        
        // Get archives, if there are any
        Element arch = DOMHelper.findFirstElementNode(
                        pv.getFirstChild(), "archive");
        while (arch != null)
        {
            name = DOMHelper.getSubelementString(arch, "name");
            String  url = DOMHelper.getSubelementString(arch, "url");
            int key = DOMHelper.getSubelementInt(arch, "key");
            item.addArchiveDataSource(
                CentralItemFactory.createArchiveDataSource(url, key, name));            
            arch = DOMHelper.findNextElementNode(arch, "archive");
        }
        return item;
    }
    
    /** @param ring_size The ring_size to set. */
    public final void setRingSize(int ring_size)
    {
        samples.setLiveCapacity(ring_size);
    }

    /** Start the item (subscribe, ...) */
    public final void start()
    {
        try
        {
            pv.addListener(this);
            pv.start();
        }
        catch (Exception e)
        {
        	Plugin.logException(pv.getName(), e);
        }
    }
    
    /** Stop the item (subscribe, ...) */
    public final void stop()
    {
        pv.removeListener(this);
        pv.stop();
    }

    /** @see org.csstudio.utility.pv.PVListener#pvDisconnected(org.csstudio.utility.pv.PV) */
    public void pvDisconnected(PV pv)
    {
        synchronized (this)
        {
            current_value = null;
        }
    }

    /** PVListener, memorize the most recent value. */
    public void pvValueUpdate(PV pv)
    {
        synchronized (this)
        {
            current_value = pv.getValue();
        }
    }

    /** Add the most recent life value of the PV to the sample sequence. */
    public final void addCurrentValueToSamples(ITimestamp now)
    {
        synchronized (this)
        {  
            // When disconnected, add one(!) end sample.
            if (current_value == null)
                samples.markCurrentlyDisconnected(now);
            else
            {   // Add the most recent value after tweaking its
                // time stamp to be 'now'
                if (current_value instanceof DoubleValue)
                    current_value = new DoubleValue(now,
                                    current_value.getSeverity(),
                                    current_value.getStatus(),
                                    current_value.getMetaData(),
                                    ((DoubleValue)current_value).getValues());
                else if (current_value instanceof EnumValue)
                    current_value = new EnumValue(now,
                                    current_value.getSeverity(),
                                    current_value.getStatus(),
                                    current_value.getMetaData(),
                                    ((EnumValue)current_value).getValues());
                else if (current_value instanceof IntegerValue)
                    current_value = new IntegerValue(now,
                                    current_value.getSeverity(),
                                    current_value.getStatus(),
                                    current_value.getMetaData(),
                                    ((IntegerValue)current_value).getValues());
                else if (current_value instanceof StringValue)
                    current_value = new StringValue(now,
                                    current_value.getSeverity(),
                                    current_value.getStatus(),
                                    current_value.format());
                else
                    Plugin.logError("ModelItem cannot update timestamp of type " //$NON-NLS-1$
                                    + current_value.getClass().getName());
                samples.addLiveSample(current_value);
                MetaData meta = current_value.getMetaData();
                if (meta instanceof NumericMetaData)
                {
                    String new_units =  ((NumericMetaData)meta).getUnits();
                    if (! units.equals(new_units))
                    {
                        units = new_units;
                        // Notify model of change, but in the UI thread
                        Display.getDefault().asyncExec(new Runnable()
                        {
                            public void run()
                            {
                                model.fireEntryLookChanged(ModelItem.this);
                            }
                        });
                    }
                }
            }
        }
    }
    
    private void clearSampleCache()
    {
    	// If data is cached we should clear the cache.
    	ArchiveCache cache = ArchiveCache.getInstance();
    	
		for (int i=0; i < archives.size(); ++i) 
		{
			try {
				ArchiveServer server = cache.getServer(archives.get(i).getUrl());
				if(server != null && server instanceof CachingArchiveServer)
				{
					// Clear server cache.
					((CachingArchiveServer)server).clearCache();
					// Clear samples archive.
					this.samples.clearArchive();
				}
			}
			catch(Exception e) { // Don't do anything. 
			}
		}
    }
    
    @Override
    @SuppressWarnings("nls")
    public String toString()
    {
        return "ModelItem(" + name + ")";
    }
}
