package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.IIntegerValue;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.util.xml.DOMHelper;
import org.csstudio.util.xml.XMLHelper;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.csstudio.utility.pv.epics.EPICS_V3_PV;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Element;

/** One item (channel, PV, line, ...) of the <code>ChartModel</code>
 *  @see Model
 *  @author Kay Kasemir
 */
public class ModelItem 
       extends AbstractModelItem
       implements IModelItem, PVListener, IProcessVariable
{	
    /** For unit tests within this package,
     *  ModelItem can directly use EPICS_V3_PV,
     *  because the extension mechanism used by the PVFactory
     *  won't work in simple unit tests outside of an Eclipse runtime.
     */
    static public boolean test_mode = false;
    
    /** The control system PV from which to get new values. */
    private PV pv;
    
    /** The most recent value we got from the pv.
     *  <p>
     *  Read from the GUI thread, updated from a PV monitor thread.
     */
    private volatile IValue current_value;
    
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
            boolean visible,
            boolean auto_scale,
            int red, int green, int blue,
            int line_width,
            TraceType trace_type,
            boolean log_scale)
    {
        super(model, pv_name, axis_index, min, max, visible, auto_scale,
              red, green , blue, line_width, trace_type, log_scale);
        pv = createPV(pv_name);
        samples = new ModelSamples(ring_size);
    }

    @SuppressWarnings("nls")
    private PV createPV(String pv_name)
    {
        if (test_mode)
            return new EPICS_V3_PV(pv_name);
        try
        {
            return PVFactory.createPV(pv_name);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Plugin.logException("Cannot create PV '" + pv_name + "'", ex);
        }
        return null;
    }    
    
    /** Must be called to dispose the color. */
    @Override
    public void dispose()
    {
        if (pv.isRunning())
            pv.stop();
        samples.dispose();
        super.dispose();
    }
    
    /** @see IModelItem#changeName(String) */
    @Override
    public void changeName(String new_name)
    {
        // Avoid duplicates, do not allow if new name already in model.
        if (model.findEntry(new_name) >= 0)
            return;
        boolean was_running = pv.isRunning();
        // TODO: I've seen null pointer errors in this stop() call,
        // but have not been able to reproduce them.
        if (was_running)
            stop();
        // Name change looks like remove/add back in
        model.fireEntryRemoved(this);
        // Now change name
        name = new_name;
        pv = createPV(name);
        // and add
        model.fireEntryAdded(this);
        if (was_running)
            start();
    }

    /** @see IModelItem#getSamples() */
    public final IModelSamples getSamples()
    {
        return samples;
    }
    
    /** @see IModelItem#addSamples() */
    @SuppressWarnings("nls")
    public void addArchiveSamples(ArchiveValues archive_samples)
    {
        samples.add(archive_samples);
    }
    
    /** @return Returns an XML string for this item.
     *  @see #loadFromDOM(Model, Element, int)
     */
    @SuppressWarnings("nls")
    public final String getXMLContent()
    {
        StringBuffer b = new StringBuffer();
        b.append("        <" + TAG_PV + ">\n");
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
        if (archives.size() > 0)
        {
            for (IArchiveDataSource archive : archives)
            {
                XMLHelper.indent(b, 3);b.append("<" + TAG_ARCHIVE + ">\n");
                XMLHelper.XML(b, 4, TAG_NAME, archive.getName());
                XMLHelper.XML(b, 4, TAG_URL, archive.getUrl());
                XMLHelper.XML(b, 4, TAG_KEY, Integer.toString(archive.getKey()));
                XMLHelper.indent(b, 3);b.append("</" + TAG_ARCHIVE + ">\n");
            }
        }
        b.append("        </" + TAG_PV + ">\n");
        return b.toString();
    }
    
    /** Decode XML DOM element for "pv ...".
     *  @see #getXMLContent()
     */
    @SuppressWarnings("nls")
    public static ModelItem loadFromDOM(Model model, Element pv, int ring_size) throws Exception
    {
        // Common PV stuff
        String name = DOMHelper.getSubelementString(pv, TAG_NAME);
        int axis_index = DOMHelper.getSubelementInt(pv, TAG_AXIS, 0);
        int line_width = DOMHelper.getSubelementInt(pv, TAG_LINEWIDTH, 0);
        double min = DOMHelper.getSubelementDouble(pv, TAG_MIN, 0.0);
        double max = DOMHelper.getSubelementDouble(pv, TAG_MAX, 10.0);
        boolean visible = DOMHelper.getSubelementBoolean(pv, TAG_VISIBLE, true);
        boolean auto_scale = DOMHelper.getSubelementBoolean(pv, TAG_AUTOSCALE);
        int red, green, blue;
        Element color =
            DOMHelper.findFirstElementNode(pv.getFirstChild(), TAG_COLOR);
        if (color != null)
        {
            red = DOMHelper.getSubelementInt(color, TAG_RED, 0);
            green = DOMHelper.getSubelementInt(color, TAG_GREEN, 0);
            blue = DOMHelper.getSubelementInt(color, TAG_BLUE, 255);
        }
        else
        {
            red = 0;
            green = 0;
            blue = 255;
        }
        boolean log_scale = DOMHelper.getSubelementBoolean(pv, TAG_LOG_SCALE);
        
        TraceType trace_type = TraceType.Lines;
        String trace_type_txt = DOMHelper.getSubelementString(pv, TAG_TRACE_TYPE);
        if (trace_type_txt.length() > 0)
            trace_type = TraceType.fromName(trace_type_txt);
        
        ModelItem item =
            new ModelItem(model, name, ring_size, axis_index,
                          min, max, visible, auto_scale,
                          red, green, blue, line_width, trace_type, log_scale);
        
        // Get archives, if there are any
        Element arch = DOMHelper.findFirstElementNode(
                        pv.getFirstChild(), TAG_ARCHIVE);
        while (arch != null)
        {
            name = DOMHelper.getSubelementString(arch, TAG_NAME);
            String  url = DOMHelper.getSubelementString(arch, TAG_URL);
            int key = DOMHelper.getSubelementInt(arch, TAG_KEY);
            item.addArchiveDataSource(
                CentralItemFactory.createArchiveDataSource(url, key, name));            
            arch = DOMHelper.findNextElementNode(arch, TAG_ARCHIVE);
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
                IValue.Quality quality = IValue.Quality.Original;
                if (current_value instanceof IDoubleValue)
                    current_value = ValueFactory.createDoubleValue(now,
                                    current_value.getSeverity(),
                                    current_value.getStatus(),
                                    (INumericMetaData)current_value.getMetaData(),
                                    quality,
                                    ((IDoubleValue)current_value).getValues());
                else if (current_value instanceof IEnumeratedValue)
                    current_value = ValueFactory.createEnumeratedValue(now,
                                    current_value.getSeverity(),
                                    current_value.getStatus(),
                                    (IEnumeratedMetaData)current_value.getMetaData(),
                                    quality,
                                    ((IEnumeratedValue)current_value).getValues());
                else if (current_value instanceof IIntegerValue)
                    current_value = ValueFactory.createIntegerValue(now,
                                    current_value.getSeverity(),
                                    current_value.getStatus(),
                                    (INumericMetaData)current_value.getMetaData(),
                                    quality,
                                    ((IIntegerValue)current_value).getValues());
                else if (current_value instanceof IStringValue)
                    current_value = ValueFactory.createStringValue(now,
                                    current_value.getSeverity(),
                                    current_value.getStatus(),
                                    quality,
                                    current_value.format());
                else
                    Plugin.logError("ModelItem cannot update timestamp of type " //$NON-NLS-1$
                                    + current_value.getClass().getName());
                samples.addLiveSample(current_value);
                IMetaData meta = current_value.getMetaData();
                if (meta instanceof INumericMetaData)
                {
                    String new_units =  ((INumericMetaData)meta).getUnits();
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
        
    @Override
    @SuppressWarnings("nls")
    public String toString()
    {
        return "ModelItem(" + name + ")";
    }
}
